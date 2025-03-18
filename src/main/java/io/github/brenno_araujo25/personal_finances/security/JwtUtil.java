package io.github.brenno_araujo25.personal_finances.security;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.JsonSerializable.Base;

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

    public String generateToken(String username) {
        try {
            Base64.Encoder base64 = Base64.getEncoder();
            byte[] encodedSecret = base64.encode(secret.getBytes());
            Algorithm algorithm = Algorithm.HMAC256(encodedSecret);
            String token = JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error creating token", e);
        }
    }
 
    public boolean validateToken(String token, String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm)
                .withSubject(username)
                .build()
                .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Map<String, Claim> claims = getClaims(token);
        return claims.get("sub").asString();
    }

    public boolean isTokenExpired(String token) {
        Map<String, Claim> claims = getClaims(token);
        Date expirationDate = claims.get("exp").asDate();
        Date now = new Date(System.currentTimeMillis());

        return expirationDate.before(now);
    }

    @SuppressWarnings("deprecated")
    private Map<String, Claim> getClaims(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                .build()
                .verify(token)
                .getClaims();
        } catch (Exception e) {
            throw new RuntimeException("Error getting claims", e);
        }
    }

}
