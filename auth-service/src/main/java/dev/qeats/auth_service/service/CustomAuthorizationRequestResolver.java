package dev.qeats.auth_service.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final OAuth2AuthorizationRequestResolver defaultResolver;
    private String ORIGINAL_REQUEST_URI = new String();
    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        log.info("inside resolve1");
        ORIGINAL_REQUEST_URI = request.getHeader("ORIGINAL_REQUEST_URL");
//        OAuth2AuthorizationRequest originalRequest = defaultResolver.resolve(request);
//        return customizeAuthorizationRequest(defaultResolver.resolve(request, "keycloak"), request);
        return customizeAuthorizationRequest(defaultResolver.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        log.info("inside resolve2");
        return customizeAuthorizationRequest(defaultResolver.resolve(request, clientRegistrationId), request);
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest == null) {
            return null;
        }
        log.info("inside customizeAuthorizationRequest");

        // Add custom logic here, for example, add a cookie
        addCookieToResponse(request);

        // You can also modify the authorization request if needed
        String new_redirect_uri = request.getHeader("ORIGINAL_REQUEST_URL");
        if(new_redirect_uri != null) {
            OAuth2AuthorizationRequest.Builder modifiedRequest = OAuth2AuthorizationRequest.from(authorizationRequest)
                    .redirectUri(new_redirect_uri)
                    .additionalParameters(Collections.singletonMap("custom_param", "http://localhost:8765/auth/callback"));

            return authorizationRequest;

        }
        return authorizationRequest;
    }

    private void addCookieToResponse(HttpServletRequest request) {
        HttpServletResponse response = (HttpServletResponse) request.getAttribute(HttpServletResponse.class.getName());

        // Add a cookie (for example, to track the original URL)
        response.addCookie(new Cookie("customCookie", "http://localhost:8765/auth/callback"));
    }
}
