package dev.qeats.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/unsecured")
public class UnSecuredController {

    @GetMapping("/rest-api")
    public String unsecuredRestApi() {
        return "This is an unsecured REST API";
    }

}
