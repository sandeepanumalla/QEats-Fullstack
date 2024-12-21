package dev.qeats.user_service.response;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class AddressVO {


    private long id;

    @NotNull(message = "Street must not be null")
    @NotEmpty(message = "Street must not be empty")
    private String city;
    private String state;

    @NotNull(message = "Country must not be null")
    @NotEmpty(message = "Country must not be empty")
    private String country;
    private String zipCode;

    @NotNull(message = "Street must not be null")
    @NotEmpty(message = "Street must not be empty")
    private String street;
    private String landmark;
}
