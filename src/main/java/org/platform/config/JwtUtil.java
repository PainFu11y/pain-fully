package org.platform.config;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.platform.model.request.LoginRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final String secret_key = "painfullysecretkeyV3JpdGUtYS1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLUpXVA";

    private long accessTokenValidity = TimeUnit.MINUTES.toMillis(30);

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";

    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public String createToken(LoginRequest loginRequest){
        Claims claims = Jwts.claims().setSubject(loginRequest.getEmail());
        claims.put("email",loginRequest.getEmail());
        claims.put("role", loginRequest.getRole().toString());


        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.SECONDS.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(tokenCreateTime)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256,secret_key)
                .compact();
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
            throw new RuntimeException("Token expired was overed");
        } catch (Exception ex) {
            throw new RuntimeException("unauthorized");
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
            throw new RuntimeException("Token expired was overed");
        }

    }

}
