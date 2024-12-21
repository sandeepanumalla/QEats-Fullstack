package dev.qeats.auth_service.controller;

import dev.qeats.auth_service.service.KeycloakAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;


@Controller

    public class AuthController {

        @Autowired
        private KeycloakAuthService keycloakAuthService;

        @GetMapping("/auth/redirect")
        public void redirectToKeycloak(HttpServletRequest request, HttpServletResponse response) throws IOException, IOException {
            keycloakAuthService.redirectToKeycloak(request, response);
        }
    }

