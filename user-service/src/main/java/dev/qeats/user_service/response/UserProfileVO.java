package dev.qeats.user_service.response;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class UserProfileVO {
    private String userId;
    @NotNull(message = "First name cannot be empty")
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    @NotNull(message = "Last name cannot be empty")
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    @NotNull(message = "Email cannot be empty")
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    private String phoneNumber;

    @Valid
    @NotNull(message = "Addresses cannot be empty")
    private List<AddressVO> addresses;
}
