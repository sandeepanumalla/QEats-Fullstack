package dev.qeats.api_gateway.controller;

import dev.qeats.api_gateway.model.KeycloakUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@Slf4j
public class MyController {

    private final ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUrl;

    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    public MyController(ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService, WebClient webClient, ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager) {
        this.reactiveOAuth2AuthorizedClientService = reactiveOAuth2AuthorizedClientService;
        this.webClient = webClient;
        this.reactiveOAuth2AuthorizedClientManager = reactiveOAuth2AuthorizedClientManager;
    }

    private final ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager;


    @GetMapping("/login/oauth2/code/keycloak")
    public Mono<String> oauth2LoginCallback(@RequestParam("code") String code) {
        // Exchange the authorization code for an access token
        // ...
        return Mono.just("Login successful");
    }

    @GetMapping("/auth/callback")
    public String hello(@CookieValue("JWT-TOKEN") String token) {
        return "I have received the token " + token;
    }

    @GetMapping("/dummy")
    public Mono<String> dummyResponse(ServerHttpRequest request, ServerHttpResponse response) {
        return Mono.just("dummy");
    }

    @GetMapping("/auth/introspect/callback")
    public String introspect(@RequestBody Map<String, Object> body) {
        return "I have received the introspection response " + body;
    }

    @GetMapping("/signin")
    public Mono<Void> redirectHandler(ServerWebExchange serverWebExchange) {
        // Construct the redirect URI to the React app's home page

        String redirectUri = UriComponentsBuilder.fromUriString("http://localhost:3000/home")
                .build()
                .toUriString();

        AtomicReference<String> accessToken = new AtomicReference<>();
        AtomicReference<String> refreshToken = new AtomicReference<>();
        return reactiveOAuth2AuthorizedClientService
                .loadAuthorizedClient("keycloak", "username")
                .flatMap(oAuth2AuthorizedClient -> {
                    // Access the token and print it for debugging
                    if (oAuth2AuthorizedClient != null && oAuth2AuthorizedClient.getAccessToken() != null) {
                        accessToken.set(oAuth2AuthorizedClient.getAccessToken().getTokenValue());
                        refreshToken.set(oAuth2AuthorizedClient.getRefreshToken().getTokenValue());
                        System.out.println("Access Token: " + oAuth2AuthorizedClient.getAccessToken().getTokenValue());

                    } else {
                        System.out.println("No OAuth2AuthorizedClient or Access Token found!");
                    }

                    ResponseCookie accessTokenCookie = ResponseCookie.from("cookieName", accessToken.toString())
                            .httpOnly(true)
                            .path("/")
                            .maxAge(60000)
                            .build();


                    serverWebExchange.getResponse().getCookies().set("jwt-token", accessTokenCookie);
//                    serverWebExchange.getResponse().getCookies()
                    serverWebExchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    serverWebExchange.getResponse().getHeaders().setLocation(URI.create(redirectUri));
                    return serverWebExchange.getResponse().setComplete();
                });
    }

//    public Mono<Void> refreshToken() {
//        return Mono.empty();
//    }


    public Mono<Void> handleTokenRefresh(String clientRegistrationId, String principalName) {
        return reactiveOAuth2AuthorizedClientManager.authorize(
                        OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                                .principal(principalName)
                                .build())
                .flatMap(client -> {
                    if (client.getRefreshToken() != null) {
                        log.info("Refreshed Access Token: {}", client.getRefreshToken().getTokenValue());
                    }
                    return Mono.empty();
                });
    }


    // write the api for /refresh token which will fetch and return the refresh token from
    // the keycloak server
    // steps..
    // 1. build the keycloak refresh url first
    // 2. Needed is the refresh token, client_secret, client_id
    // 3. grant_type is refresh_token

        @GetMapping("/refresh-token")
        public Mono<ResponseEntity<Map<String, String>>> refreshToken(@CookieValue("REFRESH-TOKEN") String refreshToken, ServerWebExchange serverWebExchange) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId); // Replace with your client ID
        body.add("client_secret", clientSecret); // Replace with your client secret if required
        body.add("refresh_token", refreshToken);

       return webClient
                .post()
                .uri(tokenUrl)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String newAccessToken = (String) response.get("access_token");
                    String newRefreshToken = (String) response.get("refresh_token");

                    serverWebExchange.getResponse().getCookies().set("JWT-TOKEN", generateResponseCookie("JWT-TOKEN", newAccessToken));
                    serverWebExchange.getResponse().getCookies().set("REFRESH-TOKEN", generateResponseCookie("REFRESH-TOKEN", newRefreshToken));

                    // Return the tokens in the response

                    return Mono.just(ResponseEntity.ok(Map.of("message", "token has been refreshed")));
                })
                .onErrorResume(error -> {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Failed to refresh token");
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
                });
//        return Mono.just("hello");
    }


    private ResponseCookie generateResponseCookie(String cookieName, String cookieValue) {
        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(true)
                .path("/")
                .maxAge(60000)
                .build();
    }


    // write the api for api for /info for querying user info from oauth provider
    // needed is access token
    // client id
    // client secret

    @GetMapping("/info")
    @CrossOrigin("http://localhost:3000")
    private Mono<ResponseEntity<KeycloakUserInfo>> userInfo(ServerWebExchange webExchange) {
        String accessToken = webExchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null));
        }

        String userInfoUrl = "http://localhost:8003/realms/QEats/protocol/openid-connect/userinfo";

        return webClient
                .get()
                .uri(userInfoUrl)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .retrieve()
                .bodyToMono(KeycloakUserInfo.class)
                .flatMap(userInfo -> Mono.just(ResponseEntity.ok(userInfo)))
                .onErrorResume(error -> {
                    error.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
                });
    }


    // make an api for logout
    //  /logout
    // it takes the jwt and refresh token
    // app id and app secret is needed
    // keycloak logout uri is needed
    // build the webclient
    // after successfull response
    // invalidate the httponly cookies
    // return the response



    @GetMapping("/logout")
    public Mono<ResponseEntity<String>> logout(
            @CookieValue(value = "REFRESH-TOKEN") String refreshToken,
            ServerWebExchange exchange) {

        if (refreshToken == null) {
            return Mono.just(ResponseEntity.badRequest().body("Refresh token is missing."));
        }

        String revokeUrl = "http://localhost:8003/realms/QEats/protocol/openid-connect/revoke";
        String logoutUrl = String.format(
                "http://localhost:8003/realms/QEats/protocol/openid-connect/logout?post_logout_redirect_uri=http://localhost:3000/home&client_id=%s&client_secret=%s",
                clientId, clientSecret);

        MultiValueMap<String, String> revokeBody = new LinkedMultiValueMap<>();
        revokeBody.add("token", refreshToken);
        revokeBody.add("client_id", clientId);
        revokeBody.add("token_hint", "refresh_token");
        revokeBody.add("client_secret", clientSecret);

        return webClient
                .post()
                .uri(URI.create(revokeUrl))
                .body(BodyInserters.fromFormData(revokeBody))
                .retrieve()
                .toBodilessEntity()
                .then(webClient.get()
                        .uri(URI.create(logoutUrl))
                        .retrieve()
                        .toBodilessEntity()
                        .then(Mono.defer(() -> {
                            ResponseCookie jwtCookie = ResponseCookie.from("JWT-TOKEN", null)
                                    .httpOnly(true)
                                    .secure(false) // Use true in production with HTTPS
                                    .path("/")
                                    .maxAge(0) // Expire immediately
                                    .build();

                            // Clear cookies after successful logout
                            ResponseCookie refreshCookie = ResponseCookie.from("REFRESH-TOKEN", null)
                                    .httpOnly(true)
                                    .secure(false) // Use true in production with HTTPS
                                    .path("/")
                                    .maxAge(0) // Expire immediately
                                    .build();
                            exchange.getResponse().addCookie(refreshCookie);
                            exchange.getResponse().addCookie(jwtCookie);

                            return Mono.just(ResponseEntity.ok("Logged out and tokens revoked successfully."));
                        }))
                )
                .onErrorResume(error -> {
                    error.printStackTrace();
                    if(error.getMessage().contains("401 Unauthorized")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Logout process failed: " + error.getMessage()));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Logout process failed: " + error.getMessage()));
                });

    }

    @GetMapping("/initiate-login")
    public Mono<Void> initiateLogin(@RequestParam(name = "targetUrl", required = false) String targetUrl, ServerWebExchange exchange) {
        // Store the target URL in the session
        return exchange.getSession().flatMap(session -> {
            if (targetUrl != null) {
                session.getAttributes().put("TARGET_URL", targetUrl);
            }
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create("/oauth2/authorization/keycloak"));
            return exchange.getResponse().setComplete();
        });
    }

//    @GetMapping("/login-success")
//    public Mono<Void> loginSuccess(ServerWebExchange exchange, @AuthenticationPrincipal OAuth2User user) {
//        return exchange.getSession().flatMap(session -> {
//            // Retrieve the target URL from the session
//            String targetUrl = (String) session.getAttribute("TARGET_URL");
//            if (targetUrl != null) {
//                session.getAttributes().remove("TARGET_URL");
//                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
//                exchange.getResponse().getHeaders().setLocation(URI.create(targetUrl));
//                return exchange.getResponse().setComplete();
//            }
//            // Default redirect if no target URL is found
//            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
//            exchange.getResponse().getHeaders().setLocation(URI.create("/home"));
//            return exchange.getResponse().setComplete();
//        });
//    }




}
