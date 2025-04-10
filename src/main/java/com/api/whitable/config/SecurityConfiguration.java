package com.api.whitable.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(
                (csrf -> csrf.disable())
        ).authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers(
                                "/login",
                                "/registration",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/restaurant/**",
                                "/api/restaurant/findAll/**"
                        ).permitAll()
                        .anyRequest().authenticated()
        ).addFilterBefore(
                jwtRequestFilter, UsernamePasswordAuthenticationFilter.class
        ).exceptionHandling(
                excep -> excep.authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login");
                })
        );

        return http.build();
    }
}
