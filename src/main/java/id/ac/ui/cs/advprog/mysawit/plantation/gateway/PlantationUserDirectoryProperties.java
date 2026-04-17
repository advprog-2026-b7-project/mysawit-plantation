package id.ac.ui.cs.advprog.mysawit.plantation.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "plantation.user-directory")
public class PlantationUserDirectoryProperties {

    private List<UserSeed> users = new ArrayList<>();

    public List<UserSeed> getUsers() {
        return users;
    }

    public void setUsers(List<UserSeed> users) {
        this.users = users;
    }

    public static class UserSeed {
        private UUID id;
        private String name;
        private String role;
        private String certificationNumber;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCertificationNumber() {
            return certificationNumber;
        }

        public void setCertificationNumber(String certificationNumber) {
            this.certificationNumber = certificationNumber;
        }
    }
}
