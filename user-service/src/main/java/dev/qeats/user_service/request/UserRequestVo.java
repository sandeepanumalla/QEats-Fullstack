package dev.qeats.user_service.request;

import dev.qeats.user_service.model.RoleType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequestVo {
    private String name;
    private String email;
    private String phoneNumber;
    private RoleType role;
}
