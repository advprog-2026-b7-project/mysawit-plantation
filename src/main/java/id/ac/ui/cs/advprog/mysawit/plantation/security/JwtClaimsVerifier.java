package id.ac.ui.cs.advprog.mysawit.plantation.security;

public interface JwtClaimsVerifier {

    /**
     * Parses and verifies a signed JWT, then returns the {@code role} claim.
     *
     * <p>Throws {@link id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException}
     * when the token is invalid, expired, or does not carry a role claim.
     *
     * @param token the compact JWT string (without the "Bearer " prefix)
     * @return the role value encoded in the token's claims
     */
    String extractRole(String token);
}
