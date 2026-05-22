package id.ac.ui.cs.advprog.mysawit.plantation.security;

import id.ac.ui.cs.advprog.mysawit.plantation.exception.ForbiddenException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAdminGuard {

    private final JwtClaimsVerifier claimsVerifier;

    @Value("${inter.service.api-key}")
    private String internalApiKey;

    public void requireAdmin(String authorizationHeader) {
        // Allow inter-service calls from trusted backends (e.g. mysawit-delivery)
        // that send the raw internal API key instead of a Bearer JWT.
        if (authorizationHeader != null && authorizationHeader.equals(internalApiKey)) {
            return;
        }

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
