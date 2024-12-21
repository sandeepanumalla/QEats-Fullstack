package dev.qeats.auth_service.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/signout")
public class LogoutController {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String keycloakIssuerUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    private static final String DEFAULT_REDIRECT_URL = "http://localhost:8081/unsecured/rest-api";

    @GetMapping
    public ModelAndView handleLogout(HttpServletRequest request, HttpServletResponse response) {
        // Fetch ID token from the cookies
        String idToken = getIdTokenFromCookies(request);

        if (idToken == null) {
            // Handle case when idToken is not found, maybe redirect to an error page
            return new ModelAndView("redirect:/error");
        }

        // Invalidate Security Context
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // Clear cookies
        clearCookies(request, response);

        // Redirect to Keycloak logout endpoint with post logout redirect URI
        String keycloakLogoutUrl = String.format("%s/protocol/openid-connect/logout?&post_logout_redirect_uri=%s&client_id=%s",
                keycloakIssuerUri,
//                idToken,
                DEFAULT_REDIRECT_URL,
                clientId);

        return new ModelAndView("redirect:" + keycloakLogoutUrl);
    }

    // Helper method to extract the ID token from cookies
    private String getIdTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ID-TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // Return null if the token is not found
    }

    // Method to clear all cookies
    private void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookie.setValue(null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
