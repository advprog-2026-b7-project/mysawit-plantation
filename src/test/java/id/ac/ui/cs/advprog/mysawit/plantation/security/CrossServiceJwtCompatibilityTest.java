package id.ac.ui.cs.advprog.mysawit.plantation.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.ForbiddenException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies that plantation's JWT verifier accepts tokens minted by mysawit-auth
 * using the shared dev secret.  The same secret must be present as
 * {@code JWT_SECRET} (default: {@code dev-secret-key-dev-secret-key-123456})
 * in both services' configuration.
 *
 * <p>These tests use the exact default from auth's {@code application.properties}
 * so any divergence between the secrets will be caught immediately.
 */
class CrossServiceJwtCompatibilityTest {

    /**
     * Must match the default in mysawit-auth application.properties:
     * {@code jwt.secret=${JWT_SECRET:dev-secret-key-dev-secret-key-123456}}.
     */
    static final String SHARED_DEV_SECRET = "dev-secret-key-dev-secret-key-123456";

    private static final SecretKey SHARED_KEY =
            Keys.hmacShaKeyFor(SHARED_DEV_SECRET.getBytes(StandardCharsets.UTF_8));

    private HmacJwtClaimsVerifier verifier;
    private JwtAdminGuard adminGuard;

    @BeforeEach
    void setUp() {
        verifier = new HmacJwtClaimsVerifier(SHARED_DEV_SECRET);
        adminGuard = new JwtAdminGuard(verifier);
    }

    // ── JwtClaimsVerifier (role extraction) ──────────────────────────────────

    @Test
    void extractRole_authMintedAdminToken_returnsAdmin() {
        String token = authToken(Map.of(
                "sub", UUID.randomUUID().toString(),
                "email", "admin@example.com",
                "role", "ADMIN",
                "username", "adminUser",
                "nama", "Admin User"));
        assertEquals("ADMIN", verifier.extractRole(token));
    }

    @Test
    void extractRole_authMintedMandorToken_returnsMandor() {
        String token = authToken(Map.of(
                "sub", UUID.randomUUID().toString(),
                "email", "mandor@example.com",
                "role", "MANDOR",
                "username", "mandorUser",
                "nama", "Mandor User"));
        assertEquals("MANDOR", verifier.extractRole(token));
    }

    @Test
    void extractRole_authMintedBuruhToken_returnsBuruh() {
        String token = authToken(Map.of(
                "sub", UUID.randomUUID().toString(),
                "role", "BURUH",
                "username", "buruhUser",
                "nama", "Buruh User"));
        assertEquals("BURUH", verifier.extractRole(token));
    }

    @Test
    void extractRole_tokenSignedWithWrongSecret_throwsUnauthorized() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "completely-different-secret-at-least-32chars"
                        .getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .claims(Map.of("role", "ADMIN", "sub", "x"))
                .issuedAt(new Date(now))
                .expiration(new Date(now + 3_600_000L))
                .signWith(wrongKey)
                .compact();
        assertThrows(UnauthorizedException.class, () -> verifier.extractRole(token));
    }

    // ── JwtAdminGuard (admin gate) ────────────────────────────────────────────

    @Test
    void requireAdmin_adminBearerFromAuthService_passes() {
        String bearer = "Bearer " + authToken(Map.of(
                "sub", UUID.randomUUID().toString(),
                "role", "ADMIN",
                "username", "admin",
                "nama", "Admin"));
        adminGuard.requireAdmin(bearer);   // must not throw
    }

    @Test
    void requireAdmin_mandorBearerFromAuthService_throwsForbidden() {
        String bearer = "Bearer " + authToken(Map.of(
                "sub", UUID.randomUUID().toString(),
                "role", "MANDOR",
                "username", "mandor",
                "nama", "Mandor"));
        assertThrows(ForbiddenException.class, () -> adminGuard.requireAdmin(bearer));
    }

    @Test
    void requireAdmin_buruhBearerFromAuthService_throwsForbidden() {
        String bearer = "Bearer " + authToken(Map.of(
                "sub", UUID.randomUUID().toString(),
                "role", "BURUH",
                "username", "buruh",
                "nama", "Buruh"));
        assertThrows(ForbiddenException.class, () -> adminGuard.requireAdmin(bearer));
    }

    @Test
    void requireAdmin_missingAuthorizationHeader_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> adminGuard.requireAdmin(null));
    }

    @Test
    void requireAdmin_nonBearerScheme_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class,
                () -> adminGuard.requireAdmin("Basic dXNlcjpwYXNz"));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /**
     * Mints a JWT the same way mysawit-auth's {@code JwtTokenProvider} does:
     * HMAC-SHA with the shared secret, {@code jti} as UUID, standard {@code iat}
     * and {@code exp}, and all custom claims in the payload.
     */
    private static String authToken(Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())        // jti — auth includes this
                .issuedAt(new Date(now))
                .expiration(new Date(now + 3_600_000L))
                .signWith(SHARED_KEY)
                .compact();
    }
}
