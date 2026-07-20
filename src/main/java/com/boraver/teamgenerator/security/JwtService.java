package com.boraver.teamgenerator.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtService {
  private final Key key;
  private final long expirationMs;

  public JwtService(String secret, long expirationMs) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expirationMs = expirationMs;
  }

  public String generateToken(String userId, String tenantId, String role, String email, String userName) {
    var now = new Date();
    var exp = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .setSubject(userId)
        .setIssuedAt(now)
        .setExpiration(exp)
        .addClaims(Map.of(
            "tenant_id", tenantId,
            "role", role,
            "email", email,
            "user_name", userName
        ))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
