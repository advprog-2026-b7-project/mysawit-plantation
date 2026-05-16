package id.ac.ui.cs.advprog.mysawit.plantation.security;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.ForbiddenException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAdminGuard {

    private final JwtClaimsVerifier claimsVerifier;

    public void requireAdmin(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException();
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        String role = claimsVerifier.extractRole(token);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ForbiddenException();
        }
    }
}
