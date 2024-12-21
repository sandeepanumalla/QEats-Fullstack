package dev.qeats.auth_service.filter;

//import dev.qeats.auth_service.service.CustomUserDetailsService;
import dev.qeats.auth_service.config.SecurityConfig;
import dev.qeats.auth_service.service.CustomOauth2UserService;
import dev.qeats.auth_service.service.CustomUserDetailsService;
import dev.qeats.auth_service.service.JwtService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
//    private final CustomUserDetailsService customUserDetailsService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    private final CustomOauth2UserService oAuth2UserService;

    public JwtFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService, ClientRegistrationRepository clientRegistrationRepository, CustomOauth2UserService oAuth2UserService) {
        this.jwtService = jwtService;
        this.clientRegistrationRepository = clientRegistrationRepository;
//        this.customUserDetailsService = customUserDetailsService;
        this.oAuth2UserService = oAuth2UserService;
    }


    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        SecurityConfig.unsecuredPaths.addAll(List.of("/login", "/logout"));

//        if(SecurityConfig.unsecuredPaths.contains(request.getRequestURI()
//        )
//                || List.of("/logout", "/introspect").contains(request.getRequestURI())
//        ) {
//            log.info("Unsecured path: {} skipping filter" , request.getRequestURI());
//            filterChain.doFilter(request, response);
//        } else {
//            String jwtToken = extractTokenFromCookies(request);
//
//            if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                // Validate the token and extract user details
//
//                OAuth2AccessToken oAuth2AccessToken = jwtService.getOAuth2AccessToken(jwtToken);
//
//                // Create OAuth2UserRequest and load user details using OAuth2UserService
//                OAuth2UserRequest userRequest = new OAuth2UserRequest(
//                        clientRegistrationRepository.findByRegistrationId("keycloak"),
//                        oAuth2AccessToken);
//
//                OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
//
//                // Create OAuth2AuthenticationToken
//                OAuth2AuthenticationToken oauthToken = new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), "keycloak");
//
//                // Set authentication in the context
//                SecurityContextHolder.getContext().setAuthentication(oauthToken);
//            }

            filterChain.doFilter(request, response);
//        }

    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "JWT-TOKEN".equals(cookie.getName())) // Look for the JWT-TOKEN cookie
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
