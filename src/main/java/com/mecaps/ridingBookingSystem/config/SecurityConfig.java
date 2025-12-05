package com.mecaps.ridingBookingSystem.config;

import com.mecaps.ridingBookingSystem.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ---------------- PUBLIC AUTH APIS ----------------
                        .requestMatchers(
                                "/",
                                "/error",
                                "/auth/login",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/auth/refresh",
                                "/user/create"
                        ).permitAll()

                        // ---------------- PAYMENT UI PAGES (Browser Pages) --------------
                        .requestMatchers(
                                "/payment/payNow",
                                "/payment/successPage"
                        ).permitAll()

                        // ---------------- PAYMENT REST APIs (Postman/Frontend) ---------
                        .requestMatchers(
                                "/api/payment/createOrder",
                                "/api/payment/success",
                                "/api/payment/completePayment",
                                "/api/payment/get-ride/**"
                        ).permitAll()
                        // ---- MAP ----
                                .requestMatchers(
                                        "/map",
                                        "/map-api/get-fare-distance",
                                        "/map-api/nearby-drivers/**",
                                        "/map-api/request-ride"
                ).permitAll()

                        // ---------------- STATIC CONTENT ----------------
                        .requestMatchers("/js/**", "/css/**", "/images/**", "/webjars/**").permitAll()

                        // ------- ADMIN SECURED ROUTES --------
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ------- DRIVER ACCESS --------
                        .requestMatchers("/driver/**").hasRole("DRIVER")

                        // ------- RIDER ACCESS --------
                        .requestMatchers("/rider/**").hasRole("RIDER")

                        // ------- Everything else must be authenticated -------
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
