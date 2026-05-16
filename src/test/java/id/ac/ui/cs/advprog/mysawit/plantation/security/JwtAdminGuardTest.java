package id.ac.ui.cs.advprog.mysawit.plantation.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.ForbiddenException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtAdminGuardTest {

    @Mock
    private JwtClaimsVerifier claimsVerifier;

    private JwtAdminGuard guard;

    @BeforeEach
    void setUp() {
        guard = new JwtAdminGuard(claimsVerifier);
    }

    @Test
    void requireAdmin_nullHeader_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class,
                () -> guard.requireAdmin(null));
    }

    @Test
    void requireAdmin_missingBearerPrefix_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class,
                () -> guard.requireAdmin("token-without-bearer-prefix"));
    }

    @Test
    void requireAdmin_emptyStringHeader_throwsUnauthorized() {
        assertThrows(UnauthorizedException.class,
                () -> guard.requireAdmin(""));
    }

    @Test
    void requireAdmin_verifierThrowsUnauthorized_propagates() {
        when(claimsVerifier.extractRole("bad-token"))
                .thenThrow(new UnauthorizedException());
        assertThrows(UnauthorizedException.class,
                () -> guard.requireAdmin("Bearer bad-token"));
    }

    @Test
    void requireAdmin_nonAdminRole_throwsForbidden() {
        when(claimsVerifier.extractRole("mandor-token")).thenReturn("MANDOR");
        assertThrows(ForbiddenException.class,
                () -> guard.requireAdmin("Bearer mandor-token"));
    }

    @Test
    void requireAdmin_buruhRole_throwsForbidden() {
        when(claimsVerifier.extractRole("buruh-token")).thenReturn("BURUH");
        assertThrows(ForbiddenException.class,
                () -> guard.requireAdmin("Bearer buruh-token"));
    }

    @Test
    void requireAdmin_adminRole_passes() {
        when(claimsVerifier.extractRole("admin-token")).thenReturn("ADMIN");
        assertDoesNotThrow(() -> guard.requireAdmin("Bearer admin-token"));
    }

    @Test
    void requireAdmin_adminRoleLowercase_passes() {
        when(claimsVerifier.extractRole("admin-token")).thenReturn("admin");
        assertDoesNotThrow(() -> guard.requireAdmin("Bearer admin-token"));
    }
}
