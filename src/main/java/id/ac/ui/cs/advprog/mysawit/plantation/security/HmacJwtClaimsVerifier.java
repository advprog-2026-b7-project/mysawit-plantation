package id.ac.ui.cs.advprog.mysawit.plantation.security;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HmacJwtClaimsVerifier implements JwtClaimsVerifier {

    private final SecretKey secretKey;

    public HmacJwtClaimsVerifier(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String extractRole(String token) {
        Claims claims = parseClaims(token);
        Object role = claims.get("role");
        if (role == null) {
            throw new UnauthorizedException();
        }
        return role.toString();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            throw new UnauthorizedException();
        }
    }
}
