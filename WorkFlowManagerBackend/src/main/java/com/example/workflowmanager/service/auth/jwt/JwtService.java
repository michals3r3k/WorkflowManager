package com.example.workflowmanager.service.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService
{
    private static final int TOKEN_LIFE_TIME_HOURS = 5;

    @Value("${jwtSecretKey.value}")
    private String secretKey;

    public String generateToken(String email)
    {
        return generateToken(email, Collections.emptyMap());
    }

    public String generateToken(String email,
        Map<String, Object> extraClaims)
    {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate = now.plusHours(TOKEN_LIFE_TIME_HOURS);
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(email)
            .setIssuedAt(toDate(now))
            .setExpiration(toDate(expirationDate))
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public boolean isTokenValid(String token, String email)
    {
        String username = getEmail(token);
        return username.equals(email) && !isTokenExpired(token);
    }

    public String getEmail(String token)
    {
        return getClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token)
    {
        Date now = new Date();
        return getClaims(token).getExpiration().before(now);
    }

    private Claims getClaims(String token)
    {
        return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    private static Date toDate(LocalDateTime dateTime)
    {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
