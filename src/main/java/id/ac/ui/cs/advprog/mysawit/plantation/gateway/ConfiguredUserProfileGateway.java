package id.ac.ui.cs.advprog.mysawit.plantation.gateway;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ConfiguredUserProfileGateway implements UserProfileGateway {

    private final PlantationUserDirectoryProperties properties;

    public ConfiguredUserProfileGateway(PlantationUserDirectoryProperties properties) {
        this.properties = properties;
    }

    @Override
    public Optional<UserProfile> findById(UUID userId) {
        return properties.getUsers().stream()
                .filter(userSeed -> userId.equals(userSeed.getId()))
                .findFirst()
                .map(userSeed -> new UserProfile(
                        userSeed.getId(),
                        userSeed.getName(),
                        userSeed.getRole(),
                        userSeed.getCertificationNumber()
                ));
    }
}
