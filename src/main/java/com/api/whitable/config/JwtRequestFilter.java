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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
                // Если accessToken валиден
                String username = jwtTokenService.getUsername(accessToken);
                User user = userRepository.findByEmail(username);

                if (user != null) {
                    setAuthentication(user, request);

                    if (user.getIsAdmin() != null) {
                        request.setAttribute("isAdmin", user.getIsAdmin());
                    }
                }

            } else if (refreshToken != null && jwtTokenService.validateToken(refreshToken)) {
                // Если accessToken истёк, но refreshToken валиден
                String username = jwtTokenService.getUsername(refreshToken);
                User user = userRepository.findByEmail(username);

                if (user != null) {
                    // Генерируем новый accessToken
                    String newAccessToken = jwtTokenService.generateAccessToken(username);
                    setAccessTokenInCookie(newAccessToken, response);
                    setAuthentication(user, request);

                    if (user.getIsAdmin() != null) {
                        request.setAttribute("isAdmin", user.getIsAdmin());
                    }
                }

            } else {
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, null);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        request.setAttribute("isAdmin", user.getIsAdmin());
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
