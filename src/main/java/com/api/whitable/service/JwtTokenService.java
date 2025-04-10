package com.api.whitable.service;

import com.api.whitable.dto.AuthDto;
import com.api.whitable.model.User;
import com.api.whitable.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final UserRepository userRepository;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.accessLifetime}")
    private Duration accessLifetime;

    @Value("${jwt.refreshLifetime}")
    private Duration refreshLifetime;
    public void generateTokens(HttpServletRequest request, HttpServletResponse response, AuthDto dto) throws JwtException {
        Map<String, Object> claims = new HashMap<>();
        Date issuedDate = new Date();
        Date accessExpirationDate = new Date(issuedDate.getTime() + accessLifetime.toMillis());
        Date refreshExpirationDate = new Date(issuedDate.getTime() + refreshLifetime.toMillis());

        // Формируем access token
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(dto.getEmail())
                .setIssuedAt(issuedDate)
                .setExpiration(accessExpirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Формируем refresh token
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(dto.getEmail())
                .setIssuedAt(issuedDate)
                .setExpiration(refreshExpirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // Проверяем, существует ли пользователь в БД
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            throw new JwtException("Ошибка при создании JWT токена: пользователь не найден");
        }

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(accessTokenCookie);
    }

    public boolean validateToken(String jwt) {
        if (jwt == null) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Извлекает refresh-токен для текущего пользователя из БД (по access токену в куках).
     */
    public String getRefreshTokenFromDb(HttpServletRequest request) {
        String jwt = getJwtFromCookies(request);
        if (jwt == null) {
            throw new IllegalArgumentException("Access токен отсутствует (куки пустые)");
        }

        String username = getUsernameToAccessToken(jwt);
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Не удалось определить пользователя из токена");
        }

        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new IllegalArgumentException("Неверное имя пользователя в токене (пользователь не существует)");
        }

        return user.getRefreshToken();
    }

    /**
     * Генерирует новый access token для пользователя (используется при рефреше).
     */
    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + accessLifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(issuedDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public String getUsernameByRequest(HttpServletRequest request) {
        String accessToken = getJwtFromCookies(request);
        if (accessToken == null) {
            throw new IllegalArgumentException("Access токен отсутствует (cookie не содержат accessToken)");
        }
        // Получаем имя пользователя из токена
        return getUsername(accessToken);
    }

    public String getUsernameToAccessToken(String token) {
        try {
            return getAllClaimsFromToken(token).getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Long getUserIdFromToken(HttpServletRequest request) {
        String accessToken = getJwtFromCookies(request);
        if (accessToken == null) {
            throw new IllegalArgumentException("Access токен отсутствует в куках.");
        }

        String email = getUsername(accessToken);
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Не удалось извлечь email из токена.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь с email " + email + " не найден.");
        }

        return user.getId();
    }
}
