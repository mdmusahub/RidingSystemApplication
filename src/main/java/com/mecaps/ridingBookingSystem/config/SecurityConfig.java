package com.mecaps.ridingBookingSystem.config;

import com.mecaps.ridingBookingSystem.security.CustomAccessDeniedHandler;
import com.mecaps.ridingBookingSystem.security.jwt.JwtAuthFilter;
import com.mecaps.ridingBookingSystem.security.jwt.JwtAuthenticationEntryPoint;
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
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthFilter jwtFilter, JwtAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilerChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .exceptionHandling((exceptions) ->
                                exceptions
                                        .authenticationEntryPoint(authenticationEntryPoint)
                                        .accessDeniedHandler(accessDeniedHandler)
                        )
                .csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.
                requestMatchers("/user/create",
                        "/auth/login",
                        "/auth/forgot-password",
                        "/auth/reset-password",
                        "/auth/refresh").permitAll()
                .requestMatchers("/map/**").permitAll()
                .requestMatchers("/admin/**",
                        "/user/getAll",
                        "/rider/getAll",
                        "/driver/getAll").hasRole("ADMIN")
                .requestMatchers("/rider/**").hasRole("RIDER")
                .requestMatchers("/driver/**").hasRole("DRIVER")
                .anyRequest().authenticated());

        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
