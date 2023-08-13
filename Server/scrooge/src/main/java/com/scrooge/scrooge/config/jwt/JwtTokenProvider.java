package com.scrooge.scrooge.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

//    @Value("${jwt.expiration}")
    private static Long expirationTime = 1000L * 60; /* * 30; // 30분 */
    private static Long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 7;
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /* 토큰을 생성하는 메서드*/
    public String createToken(String email, Long memberId) {

        Date now = new Date();
        Date period = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("memberId", memberId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(period)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* RefreshToken을 발행하는 메서드 */
    public String createRefreshToken() {
        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

//    /* refreshToken이 만료되었는지 확인 */
//    public boolean validateRefreshToken(String refreshToken){
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//
//    }

    public String extractToken(String header) {
        if (header != null && header.startsWith("Bearer")) {
            return header.substring(7);
        }
        return null;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    public Long extractMemberId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("memberId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
//



}
