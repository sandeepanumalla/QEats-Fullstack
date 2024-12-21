package dev.qeats.auth_service.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class CustomOAuth2SuccessAuthenticationHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JwtService jwtService;

    public CustomOAuth2SuccessAuthenticationHandler(OAuth2AuthorizedClientService authorizedClientService, JwtService jwtService) {
        this.authorizedClientService = authorizedClientService;
        this.jwtService = jwtService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess  for {} ", request.getRequestURI());
        HttpSession session = request.getSession();

        // Retrieve the original request URL from the session
        String originalRequestUrl = "http://localhost:8083/dummy";
        session.removeAttribute("ORIGINAL_REQUEST_URL");

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            // Load the authorized client to get the tokens
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            if (client != null) {
                OidcUser oidcUser = (OidcUser) oauthToken.getPrincipal();
                String idToken = oidcUser.getIdToken().getTokenValue();
                String accessToken = client.getAccessToken().getTokenValue();
                String refreshToken = client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : "No refresh token available";

                // Perform your custom logic here with the tokens
                System.out.println("Access Token: " + accessToken);
                System.out.println("Refresh Token: " + refreshToken);
                System.out.println("Oidc Token: " + idToken);

                String jwtToken = jwtService.generateToken(authentication);  // Assuming this method generates a token for the authenticated user

                Cookie jwtCookie = new Cookie("JWT-TOKEN", jwtToken);

                jwtCookie.setHttpOnly(true); // Make the cookie HTTP only
                jwtCookie.setSecure(true);   // Ensure the cookie is sent over HTTPS
                jwtCookie.setPath("/");      // Cookie path
                jwtCookie.setMaxAge(7 * 24 * 60 * 60);
                // You can also store tokens or trigger additional actions

                // Add the cookie to the response
                response.addCookie(jwtCookie);

                // Optionally, you could also add the refresh token as a cookie if needed
                if (refreshToken != null) {
                    Cookie refreshTokenCookie = new Cookie("REFRESH-TOKEN", refreshToken);
                    refreshTokenCookie.setHttpOnly(true);
                    refreshTokenCookie.setSecure(true);
                    refreshTokenCookie.setDomain("localhost");
                    refreshTokenCookie.setPath("/");
                    refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // Example: 30 days
                    response.addCookie(refreshTokenCookie);
                }
                if (idToken != null) {
                    Cookie idTokenCookie = new Cookie("ID-TOKEN", idToken);
                    idTokenCookie.setHttpOnly(true);
                    idTokenCookie.setSecure(true);
                    idTokenCookie.setDomain("localhost");
                    idTokenCookie.setPath("/");
                    idTokenCookie.setMaxAge(30 * 24 * 60 * 60); // Example: 30
                    response.addCookie(idTokenCookie);
                }

                if(accessToken != null) {
                    Cookie accessTokenCookie = new Cookie("ACCESS-TOKEN", accessToken);
                    accessTokenCookie.setHttpOnly(true);
                    accessTokenCookie.setSecure(true);
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setDomain("localhost");
                    accessTokenCookie.setMaxAge(30 * 24 * 60 * 60); // Example: 30
                    response.addCookie(accessTokenCookie);
                }
            }
        }

        response.sendRedirect(originalRequestUrl != null ? originalRequestUrl : "/");
    }

    public Optional<Cookie> getCustomCookie(HttpServletRequest request) {
        // Check if cookies exist in the request
        if (request.getCookies() != null) {
            // Use stream to find the cookie with the name "customCookie"
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "customCookie".equals(cookie.getName()))
                    .findFirst();
        }
        return Optional.empty();  // Return an empty Optional if no cookies are found
    }
}
