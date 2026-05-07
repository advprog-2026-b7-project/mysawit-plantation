package id.ac.ui.cs.advprog.mysawit.plantation.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.ForbiddenException;
import id.ac.ui.cs.advprog.mysawit.plantation.exception.UnauthorizedException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class JwtAdminGuard {

    private final ObjectMapper objectMapper;

    public JwtAdminGuard(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void requireAdmin(String authorizationHeader) {
        JsonNode payload = parsePayload(authorizationHeader);
        if (!containsAdminRole(payload)) {
            throw new ForbiddenException();
        }
    }

    private JsonNode parsePayload(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException();
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new UnauthorizedException();
        }

        try {
            String padded = addBase64Padding(parts[1]);
            byte[] payloadBytes = Base64.getUrlDecoder().decode(padded);
            return objectMapper.readTree(new String(payloadBytes, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new UnauthorizedException();
        }
    }

    private String addBase64Padding(String base64) {
        int mod = base64.length() % 4;
        if (mod == 0) {
            return base64;
        }
        return base64 + "=".repeat(4 - mod);
    }

    private boolean containsAdminRole(JsonNode payload) {
        JsonNode roleNode = payload.get("role");
        if (roleNode != null
                && roleNode.isTextual()
                && "ADMIN".equalsIgnoreCase(roleNode.asText())) {
            return true;
        }

        JsonNode rolesNode = payload.get("roles");
        if (rolesNode != null) {
            if (rolesNode.isArray()) {
                for (JsonNode role : rolesNode) {
                    if (role.isTextual()
                            && "ADMIN".equalsIgnoreCase(role.asText())) {
                        return true;
                    }
                }
            }
            if (rolesNode.isTextual() && "ADMIN".equalsIgnoreCase(rolesNode.asText())) {
                return true;
            }
        }

        return false;
    }
}
