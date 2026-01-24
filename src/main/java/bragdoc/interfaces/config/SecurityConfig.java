package bragdoc.interfaces.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import bragdoc.infrastructure.security.JwtAuthenticationFilter;
import bragdoc.infrastructure.security.TokenRefreshFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenRefreshFilter tokenRefreshFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            TokenRefreshFilter tokenRefreshFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.tokenRefreshFilter = tokenRefreshFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(tokenRefreshFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, TokenRefreshFilter.class);

        return http.build();
    }
}
