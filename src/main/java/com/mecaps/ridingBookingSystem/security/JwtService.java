package com.mecaps.ridingBookingSystem.security;

import com.mecaps.ridingBookingSystem.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private final static long ACCESS_TOKEN_EXP = 1000 * 60 * 60;
    private final static String SECRET_KEY = "a4bf05ff532862e43eaee226f19619674f8d89a7d5442e426a6e2e170e731a66";

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateRefreshToken(String email, String role){
        return generateAccessToken(email,role);
    }

    public String generateAccessToken(String email, String role){
        return Jwts.builder()
                .subject(email)
                .claim("role",role)
                .claim("Access Token",String.class)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .signWith(getSecretKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token){
        return extractAllClaims(token).get("role",String.class);
    }

    public boolean isTokenValid(String token){
        try{
        return extractAllClaims(token).getExpiration().after(new Date());
    }catch (Exception e) {
            return false;
        }

    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseClaimsJws(token)
                 .getPayload();
    }

    public String createResetPasswordToken(String email) {

        long resetExpiry = 1000*60*15;
        return Jwts.builder()
                .subject(email)
                .claim("type","Reset_Password")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+resetExpiry))
                .signWith(getSecretKey())
                .compact();
    }

//    public void markTokenAsUsed(String token) {
//
//
//    }
//
//    public Optional<User> getUserByResetToken(String token) {
//    }

}
