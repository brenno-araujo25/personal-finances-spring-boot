package io.github.brenno_araujo25.personal_finances.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        SecretKey key = getSigningKey();

        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact();
    }
 
    public boolean validateToken(String token, String username) {
        Claims claims = getClaims(token);
        String tokenUsername = claims.getSubject();
        Date expirationDate = claims.getExpiration();
        Date now = new Date(System.currentTimeMillis());

        return tokenUsername.equals(username) && !expirationDate.before(now);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getClaims(token);
        Date expirationDate = claims.getExpiration();
        Date now = new Date(System.currentTimeMillis());

        return expirationDate.before(now);
    }

    private Claims getClaims(String token) {
        SecretKey key = getSigningKey();

        return Jwts.parser()
            .decryptWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

}
