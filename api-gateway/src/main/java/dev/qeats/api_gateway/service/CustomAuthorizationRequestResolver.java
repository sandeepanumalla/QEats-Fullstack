package dev.qeats.api_gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class CustomAuthorizationRequestResolver implements ServerOAuth2AuthorizationRequestResolver {

    private final DefaultServerOAuth2AuthorizationRequestResolver defaultResolver;
    private final ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService;

    private final ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;

    public CustomAuthorizationRequestResolver(ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService, ReactiveClientRegistrationRepository reactiveClientRegistrationRepository) {
        this.reactiveOAuth2AuthorizedClientService = reactiveOAuth2AuthorizedClientService;
        this.reactiveClientRegistrationRepository = reactiveClientRegistrationRepository;
        this.defaultResolver = new DefaultServerOAuth2AuthorizationRequestResolver(reactiveClientRegistrationRepository);
    }


    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange) {
        return defaultResolver.resolve(exchange)
                .flatMap(authorizationRequest -> customizeAuthorizationRequest(exchange, authorizationRequest));
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange, String clientRegistrationId) {
        return defaultResolver.resolve(exchange, clientRegistrationId)
                .flatMap(authorizationRequest -> customizeAuthorizationRequest(exchange, authorizationRequest));
    }

    private Mono<OAuth2AuthorizationRequest> customizeAuthorizationRequest(
            ServerWebExchange exchange, OAuth2AuthorizationRequest authorizationRequest) {

        log.info("Customizing authorization request for client: {}", authorizationRequest.getClientId());

        Mono<OAuth2AuthorizedClient> authorizedClient = reactiveOAuth2AuthorizedClientService.loadAuthorizedClient("keycloak", "anonymous");
        log.info("Access token {} " , handleAccessToken(exchange));

        if (authorizationRequest == null) {
            return Mono.empty();
        }

        // Extract `redirect_uri` from query parameters
        String dynamicRedirectUri = exchange.getRequest().getQueryParams().getFirst("redirect_uri");

        if (dynamicRedirectUri != null) {
            return Mono.just(OAuth2AuthorizationRequest.from(authorizationRequest)
                    .redirectUri(dynamicRedirectUri)
                    .build());
        }

        return Mono.just(authorizationRequest);
    }

    private Mono<Void> handleAccessToken(
            ServerWebExchange exchange) {
        Mono<OAuth2AuthorizedClient> authorizedClient = reactiveOAuth2AuthorizedClientService.loadAuthorizedClient("keycloak", "anonymous");

        return authorizedClient.flatMap(client -> {
            if (client != null && client.getAccessToken() != null) {
                String accessToken = client.getAccessToken().getTokenValue();

                log.info("Successfully retrieved access token for user: {}", "anonymous");

                // Redirect to the custom redirect URI with the token
                String redirectUri = "http://localhost:3000/home?accessToken=" + accessToken;
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                exchange.getResponse().getHeaders().setLocation(URI.create(redirectUri));

                return exchange.getResponse().setComplete();
            } else {
                log.error("Failed to retrieve access token for user: {}", "anonymous");
                return Mono.error(new IllegalStateException("Access token not found"));
            }
        });

    }
}
