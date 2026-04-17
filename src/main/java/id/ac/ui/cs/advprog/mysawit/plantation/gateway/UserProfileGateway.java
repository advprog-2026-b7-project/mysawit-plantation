package id.ac.ui.cs.advprog.mysawit.plantation.gateway;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileGateway {

    Optional<UserProfile> findById(UUID userId);
}
