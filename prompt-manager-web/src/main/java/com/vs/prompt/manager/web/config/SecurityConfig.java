package com.vs.prompt.manager.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Profile("dev")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection for local development and APIs
                .csrf(csrf -> csrf.disable())

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow access health endpoint
                        .requestMatchers("/health").permitAll()

                        // Allow access to H2 console
                        .requestMatchers("/h2-console/**").permitAll()

                        // Allow access to Swagger UI and OpenAPI definition
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/actuator/metrics"

                        ).permitAll()

                        // Require authentication for any /api/** endpoint
                        .requestMatchers("/api/**").authenticated()

                        // Everything else also requires authentication
                        .anyRequest().authenticated()
                )

                // Enable HTTP Basic authentication
                .httpBasic(Customizer.withDefaults())

                // Allow frames (for H2 console to work)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }




    @Bean
    public UserDetailsService users() {
        // Create in-memory user for dev profile
        UserDetails user = User.withUsername("admin")
                .password("{noop}admin123") // {noop} = no password encoder
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}
