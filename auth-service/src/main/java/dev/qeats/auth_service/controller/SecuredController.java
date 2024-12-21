package dev.qeats.auth_service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class SecuredController {

    @GetMapping("/rest-api")
    public String securedRestApi(@AuthenticationPrincipal Principal principal) {
        System.out.println(principal);
        return "This is a secured REST API";
    }



}
