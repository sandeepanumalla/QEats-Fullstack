package dev.qeats.user_service.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserInfo {
    private String sub;
    private String family_name;
    private String name;
    private String given_name;
    private String preferred_username;
    private Boolean email_verified; // Correct type
    private String email;
}
