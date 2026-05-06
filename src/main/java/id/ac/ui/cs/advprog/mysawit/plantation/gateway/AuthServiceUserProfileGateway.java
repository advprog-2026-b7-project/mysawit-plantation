package id.ac.ui.cs.advprog.mysawit.plantation.gateway;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("!test")
public class AuthServiceUserProfileGateway implements UserProfileGateway {

    private final RestTemplate restTemplate;
    private final String authServiceBaseUrl;

    public AuthServiceUserProfileGateway(
            RestTemplate restTemplate,
            @Value("${auth.service.base-url:http://localhost:8080}") String authServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.authServiceBaseUrl = authServiceBaseUrl;
    }

    @Override
    public Optional<UserProfile> findById(UUID userId) {
        String url = authServiceBaseUrl + "/api/auth/profile/" + userId;
        try {
            ResponseEntity<AuthProfileResponse> response =
                    restTemplate.getForEntity(url, AuthProfileResponse.class);
            AuthProfileResponse body = response.getBody();
            if (body == null) {
                return Optional.empty();
            }
            return Optional.of(new UserProfile(
                    UUID.fromString(body.id()),
                    body.username(),
                    body.role(),
                    body.mandorCertificationNumber()
            ));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to fetch user profile from auth service for userId=" + userId, e);
        }
    }

    private record AuthProfileResponse(
            String id,
            String email,
            String username,
            String role,
            String mandorCertificationNumber,
            String mandorId
    ) {}
}
