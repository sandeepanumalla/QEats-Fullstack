package dev.qeats.api_gateway.config;

//import dev.qeats.api_gateway.service.CustomOAuth2SuccessAuthenticationHandler;
import dev.qeats.api_gateway.filters.JwtFilter;
import dev.qeats.api_gateway.service.CustomAuthorizationRequestResolver;
import dev.qeats.api_gateway.service.CustomOAuth2SuccessAuthenticationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//
@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    private final ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager;


    public static List<String> unsecuredPaths = new ArrayList<>(List.of(
            "/auth/**",
//            "/signin/**",
            "/info/**",
            "/logout/**",
            "/initiate-login/**",
            "/refresh-token/**",
            "/unsecured/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
            "/webjars/**", "/swagger-resources/**", "/introspect"
    ));

    private static final String DEFAULT_REDIRECT_URL = "http://localhost:8081/unsecured/rest-api";

    private final JwtFilter jwtFilter;

    public SecurityConfig(ReactiveClientRegistrationRepository clientRegistrationRepository, ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager, JwtFilter jwtFilter, CustomOAuth2SuccessAuthenticationHandler customOAuth2SuccessAuthenticationHandler) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.reactiveOAuth2AuthorizedClientManager = reactiveOAuth2AuthorizedClientManager;
        this.jwtFilter = jwtFilter;
        this.customOAuth2SuccessAuthenticationHandler = customOAuth2SuccessAuthenticationHandler;
    }

    private final CustomOAuth2SuccessAuthenticationHandler customOAuth2SuccessAuthenticationHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(corsSpec -> corsSpec.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("http://localhost:3000", "null")); // Replace "*" with allowed origins
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow HTTP methods
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Allow headers
                    config.setAllowCredentials(true); // Allow cookies and credentials
                    return config;
                }))
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(unsecuredPaths.toArray(new String[0])).permitAll()
                        .anyExchange().authenticated()
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .oauth2Login(oauth2 ->
                                oauth2.authenticationSuccessHandler(customOAuth2SuccessAuthenticationHandler)
//                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                )
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()))
                .logout(logoutSpec -> logoutSpec.disable())
                // Disable default logout handling
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler() {
        OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri("http://localhost:8003/realms/master/protocol/openid-connect/logout?post_logout_redirect_uri=http://localhost:3000/home/&client_id=qeats-app");
        return logoutSuccessHandler;
    }

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:3000"); // Use specific origin
        config.addAllowedMethod("*"); // Allow all HTTP methods
        config.addAllowedHeader("*"); // Allow all headers
        config.setAllowCredentials(true); // Allow cookies and credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

}
