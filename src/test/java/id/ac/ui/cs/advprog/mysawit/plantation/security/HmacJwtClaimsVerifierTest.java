package id.ac.ui.cs.advprog.mysawit.plantation.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HmacJwtClaimsVerifierTest {

    private static final SecretKey TEST_KEY =
            Keys.hmacShaKeyFor(
                    JwtTestHelper.TEST_SECRET.getBytes(StandardCharsets.UTF_8));

    private HmacJwtClaimsVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new HmacJwtClaimsVerifier(JwtTestHelper.TEST_SECRET);
    }

    @Test
    void extractRole_validAdminToken_returnsAdmin() {
        String token = validToken(Map.of("role", "ADMIN", "sub", "user-1"));
        assertEquals("ADMIN", verifier.extractRole(token));
    }

    @Test
    void extractRole_validMandorToken_returnsMandor() {
        String token = validToken(Map.of("role", "MANDOR", "sub", "user-2"));
        assertEquals("MANDOR", verifier.extractRole(token));
    }

    @Test
    void extractRole_forgedSignature_throwsUnauthorized() {
        String real = validToken(Map.of("role", "ADMIN", "sub", "user-1"));
        String forged = real.substring(0, real.lastIndexOf('.') + 1) + "bad-signature";
        assertThrows(UnauthorizedException.class, () -> verifier.extractRole(forged));
    }

    @Test
    void extractRole_expiredToken_throwsUnauthorized() {
        String token = expiredToken(Map.of("role", "ADMIN", "sub", "user-1"));
        assertThrows(UnauthorizedException.class, () -> verifier.extractRole(token));
    }

    @Test
    void extractRole_malformedToken_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class,
                () -> verifier.extractRole("not.a.jwt"));
    }

    @Test
    void extractRole_randomString_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class,
                () -> verifier.extractRole("completely-invalid"));
    }

    @Test
    void extractRole_tokenWithoutRoleClaim_throwsUnauthorized() {
        String token = validToken(Map.of("sub", "user-1"));
        assertThrows(UnauthorizedException.class, () -> verifier.extractRole(token));
    }

    @Test
    void extractRole_wrongSecret_throwsUnauthorized() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
                "different-secret-key-for-test-min32chars"
                        .getBytes(StandardCharsets.UTF_8));
        long nowMs = System.currentTimeMillis();
        String token = Jwts.builder()
                .claims(Map.of("role", "ADMIN"))
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + 3_600_000L))
                .signWith(otherKey)
                .compact();
        assertThrows(UnauthorizedException.class, () -> verifier.extractRole(token));
    }

    private String validToken(Map<String, Object> claims) {
        long nowMs = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + 3_600_000L))
                .signWith(TEST_KEY)
                .compact();
    }

    private String expiredToken(Map<String, Object> claims) {
        long past = System.currentTimeMillis() - 7_200_000L;
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(past))
                .expiration(new Date(past + 1_000L))
                .signWith(TEST_KEY)
                .compact();
    }
}
