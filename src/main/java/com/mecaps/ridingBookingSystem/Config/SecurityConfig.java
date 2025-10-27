package com.mecaps.ridingBookingSystem.Config;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
   //   private final JwtAuthFilter jwtFilter;
    public SecurityConfig(UserDetailsService userDetailsService,JwtAuthFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }
@Bean
public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
}
@Bean
public SecurityFilterChain securityFilerChain(HttpSecurity httpSecurity) throws Exception{
    httpSecurity.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth->auth.
            requestMatchers("/user/create").permitAll().anyRequest().authenticated());

    httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
}

}
