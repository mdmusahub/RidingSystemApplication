package com.mecaps.ridingBookingSystem.security;

import io.jsonwebtoken.Claims;
<<<<<<< HEAD
import io.jsonwebtoken.JwtBuilder;
=======
import io.jsonwebtoken.JwtException;
>>>>>>> 69a85fe107ca7059f208390fd644ca470d085e3d
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final static long ACCESS_TOKEN_EXP = 2 * 60000;
    private final static String SECRET_KEY = "a4bf05ff532862e43eaee226f19619674f8d89a7d5442e426a6e2e170e731a66";
    private final static long REFRESH_TOKEN_EXP = 7 * 24 * 60 * 60 * 1000;

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateRefreshToken(String email, String role) {
        return Jwts.builder().subject(email).claim("role", role)
                .claim("type", "RefreshToken").issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP))
                .signWith(getSecretKey()).compact();

    }

    public String generateAccessToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("type", "AccessToken")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP))
                .signWith(getSecretKey())
                .compact();
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

<<<<<<< HEAD
    public String extractEmail(String token) {
        return extractAllClaims(token)
                .getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token)
                .get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        return extractAllClaims(token)
                .getExpiration().after(new Date());
=======
    public Boolean isAccesToken(String token) {
        try {
            return "access".equals(extractAllClaims(token).get("type", String.class));
        } catch (JwtException e) {
            return false;
        }
    }
    public Boolean isRefreshToken(String token){
        try{
            return "refresh".equals(extractAllClaims(token).get("type",String.class));
        }catch (JwtException e){
            return false;
        }

>>>>>>> 69a85fe107ca7059f208390fd644ca470d085e3d
    }
}



