package com.mecaps.ridingBookingSystem.config;

import com.mecaps.ridingBookingSystem.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilerChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.
                requestMatchers("/user/create").permitAll()
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/forgot-password").permitAll()
                .requestMatchers("/auth/reset-password").permitAll()
                .requestMatchers("/auth/refresh").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/rider/**").hasRole("RIDER")
                .requestMatchers("/driver/**").hasRole("DRIVER")
                .requestMatchers("/rider/getAll").hasRole("ADMIN")
                .requestMatchers("/driver/getAll").hasRole("ADMIN")
                .requestMatchers("/user/getAll").hasRole("ADMIN")
                .anyRequest().authenticated());

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
