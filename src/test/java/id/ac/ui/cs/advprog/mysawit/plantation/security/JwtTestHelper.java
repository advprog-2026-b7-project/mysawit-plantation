package id.ac.ui.cs.advprog.mysawit.plantation.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;

public final class JwtTestHelper {

    public static final String TEST_SECRET =
            "test-jwt-secret-for-mysawit-plantation-min32ch";

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtTestHelper() {
    }

    public static String signedBearer(Map<String, Object> claims) {
        long nowMs = System.currentTimeMillis();
        String compact = Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + 3_600_000L))
                .signWith(KEY)
                .compact();
        return "Bearer " + compact;
    }

    public static String expiredBearer(Map<String, Object> claims) {
        long past = System.currentTimeMillis() - 7_200_000L;
        String compact = Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(past))
                .expiration(new Date(past + 1_000L))
                .signWith(KEY)
                .compact();
        return "Bearer " + compact;
    }

    public static String adminBearer() {
        return signedBearer(Map.of(
                "role", "ADMIN",
                "sub", "11111111-1111-1111-1111-111111111111"
        ));
    }

    public static String userBearer(String role) {
        return signedBearer(Map.of(
                "role", role,
                "sub", "22222222-2222-2222-2222-222222222222"
        ));
    }
}
