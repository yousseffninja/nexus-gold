package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    public String generateToken(UserDetails userDetails) {
       return Jwts
               .builder()
               .setSubject(userDetails.getUsername())
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
               .signWith(getSigntureKey(), SignatureAlgorithm.HS256)
               .compact();
    }

    @Override
    public String generateRefreshToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {
       return Jwts
               .builder()
               .setClaims(extraClaims)
               .setSubject(userDetails.getUsername())
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
               .signWith(getSigntureKey(), SignatureAlgorithm.HS256)
               .compact();
    }

    public String extractUserName(String token) {
       return extractClaim(token, Claims::getSubject);
    }

    public String extractUserNameFromExpiredToken(String token) {
       return extractClaimFromExpiredToken(token, Claims::getSubject);
    }

     private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
       final Claims claims = extractAllClaims(token);
       return claimsResolver.apply(claims);
     }

    private <T> T extractClaimFromExpiredToken(String token, Function<Claims, T> claimsResolver) {
       final Claims claims = extractAllClaimsFromExpiredToken(token);
       return claimsResolver.apply(claims);
    }

    private Key getSigntureKey() {
           byte[] key = Decoders.BASE64.decode("QWERTYUIOPASDFGHJKLZXCVBNM1234567890abcdefABCDEFGH");
           return Keys.hmacShaKeyFor(key);
    }

    private Claims extractAllClaims(String token) {
       return Jwts
               .parser()
               .setSigningKey(getSigntureKey())
               .build()
               .parseClaimsJws(token)
               .getBody();
    }

    private Claims extractAllClaimsFromExpiredToken(String token) {
       try {
           return Jwts
                   .parser()
                   .setSigningKey(getSigntureKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
       } catch (ExpiredJwtException e) {
           return e.getClaims();
       }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
       final String username = extractUserName(token);
       return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
       return extractClaim(token, Claims::getExpiration).before(new Date());
    }

}
