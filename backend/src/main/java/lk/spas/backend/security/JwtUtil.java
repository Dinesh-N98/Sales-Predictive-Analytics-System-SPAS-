package lk.spas.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public final class JwtUtil {

    private static final long EXPIRATION_MS = 24L * 60L * 60L * 1000L;

    // Required environment variable: SPAS_JWT_SECRET.
    private static final String SECRET = loadSecret();

    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private static String loadSecret() {
        String secret = System.getenv("SPAS_JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Required environment variable SPAS_JWT_SECRET is not set");
        }
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("SPAS_JWT_SECRET must be at least 32 bytes for HS256");
        }
        return secret;
    }

    private JwtUtil() {
    }

    public static String generateToken(String subject, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token);
        return claims.getBody();
    }

    public static void validateClaims(String subject, String role) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("JWT subject is required");
        }
        try {
            if (Integer.parseInt(subject) <= 0) {
                throw new IllegalArgumentException("JWT subject must be a positive id");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("JWT subject must be a numeric id", ex);
        }
        if (!"MANAGER".equals(role) && !"EXECUTIVE".equals(role)) {
            throw new IllegalArgumentException("JWT role is invalid");
        }
    }
}
