package com.stackdarker.platform.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/v1/auth/refresh").permitAll()
                .anyRequest().authenticated()
            );

        // Later: add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build();
    }
}
