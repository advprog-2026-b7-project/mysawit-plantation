package id.ac.ui.cs.advprog.mysawit.plantation.gateway;

import java.util.UUID;

public record UserProfile(
        UUID id,
        String name,
        String email,
        String role,
        String certificationNumber
) {
}
