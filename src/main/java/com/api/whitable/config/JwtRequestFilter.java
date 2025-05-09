package com.api.whitable.config;

import com.api.whitable.model.User;
import com.api.whitable.repository.UserRepository;
import com.api.whitable.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (ignoreURL(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Пытаемся достать нужные токены
        String accessToken = null;
        String refreshToken = null;
        try {
            accessToken = jwtTokenService.getJwtFromCookies(request);
            refreshToken = jwtTokenService.getRefreshTokenFromDb(request);
        } catch (Exception e) {
            // Если упало при получении refresh-токена, тоже перенаправляем на /login
            SecurityContextHolder.clearContext();
            response.sendRedirect("/login");
            return;
        }

        try {
            if (accessToken != null && jwtTokenService.validateToken(accessToken)) {
                String username = jwtTokenService.getUsername(accessToken);
                User user = userRepository.findByEmail(username);
                String role = jwtTokenService.getAllClaimsFromToken(accessToken).get("role", String.class);

                if (user != null && role != null) {
                    setAuthentication(user, role, request);
                }

            } else if (refreshToken != null && jwtTokenService.validateToken(refreshToken)) {
                String username = jwtTokenService.getUsername(refreshToken);
                User user = userRepository.findByEmail(username);

                if (user != null) {
                    String newAccessToken = jwtTokenService.generateAccessToken(username);
                    setAccessTokenInCookie(newAccessToken, response);
                    String role = jwtTokenService.getAllClaimsFromToken(newAccessToken).get("role", String.class);
                    setAuthentication(user, role, request);
                }
            }
            else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(User user, String role, HttpServletRequest request) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void setAccessTokenInCookie(String newAccessToken, HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(accessTokenCookie);
    }

    private boolean ignoreURL(String url) {
        return url.startsWith("/css") || url.startsWith("/js") ||
                url.equals("/login") || url.equals("/registration") ||
                url.startsWith("/api/restaurant/findAll");

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
/** TODO: 06.05 - 08.05
 * - Сделать API Endpoint для "Также могут понравиться" в профиле ресторана +
 * - Перевести дни недели в разделе "Часы работы" и также сделать их в правильном порядке в профиле ресторана +
 * - Перенести создание ресторана из страницы "Рестораны" в admin-restaurants (будет сложно там лютый говнокод братик) +
 * - Сделать нормальный размер для модалки создания нового ресторана +
 * - Сделать проверки на корректность времени при создании ресторана +
 * - Сделать нормальный Footer и некоторые ссылки на нем рабочие +
 * - Сделать страницы 401 & 403 & 500 ошибки +
 * - Сделать динамическую проверку времени при записях, чтобы статус менялся +
 * - Сортировка +
 * - Количество отзывов в админке admin-bookings не работает? + Пофикшено
 * - Сделать список последних бронирований (на главной странице admin-dashboard) +


 * - Перевести header для страниц ошибок
 * - Сделать во всех Footer переход к админке
 * - Добавить больше ресторанов (~10 - 12)
 * - Наплодить всяких данных для того чтобы графики и админка смотрелась адекватно
 * - Добавить больше типов кухонь и также сделать их в фильтрации & админке и в базе данных на бэке тоже подправить

 ===================  Самый ласт проверка потом   ===========================
 * - Проверить все страницы (Все ли переведено) +-
 */