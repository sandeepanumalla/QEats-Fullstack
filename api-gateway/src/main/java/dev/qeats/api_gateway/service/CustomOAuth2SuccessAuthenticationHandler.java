package dev.qeats.api_gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;

@Component
@Slf4j
public class CustomOAuth2SuccessAuthenticationHandler implements ServerAuthenticationSuccessHandler {

    private final ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService;

    public CustomOAuth2SuccessAuthenticationHandler(ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService) {
        this.reactiveOAuth2AuthorizedClientService = reactiveOAuth2AuthorizedClientService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange exchange, Authentication authentication) {
        return exchange.getExchange().getSession()
                .flatMap(session -> {
                    String targetUrl = session.getAttribute("TARGET_URL");
                    log.info("TARGET_URL: {}", targetUrl); // Use logging to ensure better debugging
                    return Mono.justOrEmpty(targetUrl);
                })
                .flatMap(targetUrl -> {
                    if (authentication instanceof OAuth2AuthenticationToken) {
                        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
                        String principalName = oauthToken.getName();

                        // Retrieve the OAuth2AuthorizedClient
                        return reactiveOAuth2AuthorizedClientService
                                .loadAuthorizedClient(clientRegistrationId, principalName)
                                .flatMap(client -> {
                                    if (client != null && client.getAccessToken() != null) {
                                        String accessToken = client.getAccessToken().getTokenValue();
                                        String refreshToken = client.getRefreshToken() != null
                                                ? client.getRefreshToken().getTokenValue()
                                                : null;

                                        log.info("Successfully retrieved access token for user: {}", principalName);

                                        ResponseCookie accessTokenCookie = ResponseCookie.from("JWT-TOKEN", accessToken)
                                                .httpOnly(true)
                                                .path("/")
                                                .maxAge(60000)
                                                .build();

                                        ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", refreshToken)
                                                .httpOnly(true)
                                                .path("/")
                                                .maxAge(60000)
                                                .build();

                                        // Redirect to the TARGET_URL or a default URI
                                        String redirectUri = targetUrl != null ? targetUrl : "http://localhost:3000/home";
                                        exchange.getExchange().getResponse().getCookies().set("JWT-TOKEN", accessTokenCookie);
                                        exchange.getExchange().getResponse().getCookies().set("REFRESH-TOKEN", refreshTokenCookie);
                                        exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                                        exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(redirectUri));

                                        return exchange.getExchange().getResponse().setComplete();
                                    } else {
                                        log.error("Failed to retrieve access token for user: {}", principalName);
                                        return Mono.error(new IllegalStateException("Access token not found"));
                                    }
                                });
                    }
                    return Mono.empty();
                });
    }
}
