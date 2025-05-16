package org.platform.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.platform.enums.Role;
import org.platform.model.request.LoginRequest;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final String secret_key = "painfullysecretkeyV3JpdGUtYS1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUpXVA";

    private final SecretKey key = Keys.hmacShaKeyFor(secret_key.getBytes());

    private final JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();

    private long accessTokenValidity = TimeUnit.MINUTES.toMillis(30);

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public String createToken(LoginRequest loginRequest){
        Claims claims = Jwts.claims().setSubject(loginRequest.getEmail());
        claims.put("email", loginRequest.getEmail());
        claims.put("role", loginRequest.getRole().toString());

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createToken(String email, Role role) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setRole(role);

        return createToken(loginRequest);
    }

    public String getUsername(String token){
        return parsJwtClaims(token).getSubject();
    }

    public String getRole(String token){
        return parsJwtClaims(token).get("role").toString();
    }

    private Claims parsJwtClaims(String token){
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest request) {
        try {
            String token = resolveToken(request);
            if (token != null) {
                return parsJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token has expired");
        } catch (Exception ex) {
            throw new RuntimeException("Unauthorized");
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw new RuntimeException("Token has expired");
        }
    }
}
