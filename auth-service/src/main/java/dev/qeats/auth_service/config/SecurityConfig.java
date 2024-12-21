package dev.qeats.auth_service.config;

import dev.qeats.auth_service.filter.JwtFilter;
import dev.qeats.auth_service.filter.OriginalRequestUrlFilter;
import dev.qeats.auth_service.service.CustomAuthorizationRequestResolver;
import dev.qeats.auth_service.service.CustomOAuth2SuccessAuthenticationHandler;
import dev.qeats.auth_service.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.config.Customizer;import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomOAuth2SuccessAuthenticationHandler customOAuth2SuccessAuthenticationHandler;
    private final OriginalRequestUrlFilter originalRequestUrlFilter;
    private final JwtFilter jwtFilter;
    private final CustomAuthorizationRequestResolver authorizationRequestResolver;
//    private final CustomOauth2UserService customOauth2UserService;

    // write array which contains unsecured paths
    public static List<String> unsecuredPaths = new ArrayList<>(List.of("/unsecured/**", "/swagger-ui/**", "/swagger-ui.html",
            "/v3/api-docs/**", "/webjars/**", "/swagger-resources/**" , "/logout", "/logout", "/introspect",
            "/swagger-resources/configuration/ui", "/swagger-resources/configuration/security"));

//    @Value("${jwt.secret-key}")  // Fetch the secret key from application.yml or properties
//    private String secretKey;

//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withSecretKey(SecretKey.class.cast(secretKey)).build();
//    }

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository, CustomOAuth2SuccessAuthenticationHandler customOAuth2SuccessAuthenticationHandler, OriginalRequestUrlFilter originalRequestUrlFilter, JwtFilter jwtFilter, CustomUserDetailsService customUserDetailsService, CustomAuthorizationRequestResolver authorizationRequestResolver) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.customOAuth2SuccessAuthenticationHandler = customOAuth2SuccessAuthenticationHandler;
        this.originalRequestUrlFilter = originalRequestUrlFilter;
        this.jwtFilter = jwtFilter;
        this.authorizationRequestResolver = authorizationRequestResolver;
    }
    private static final String DEFAULT_REDIRECT_URL = "http://localhost:8081/unsecured/rest-api";

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService()); // Use only one UserDetailsService bean
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(Customizer.withDefaults())
                .addFilterBefore(jwtFilter, OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(originalRequestUrlFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(unsecuredPaths.toArray(new String[0]))
                                .permitAll()
                                .anyRequest().authenticated()
                )

                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(request ->
                        request.authorizationEndpoint(resolver ->
                                        resolver.authorizationRequestResolver(authorizationRequestResolver))

//                                .userInfoEndpoint(userInfoEndpoint ->
//                                userInfoEndpoint.userService(customOauth2UserService)
//                        )

                .successHandler(customOAuth2SuccessAuthenticationHandler)
                )

//                .logout(AbstractHttpConfigurer::disable)
//                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
//        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
//        log.info("is it triggered?");
//        // Define the correct post_logout_redirect_uri
//        String postLogoutRedirectUri = "http://localhost:8081/unsecured/rest-api";
//
//        // Set the post logout redirect URI
//        successHandler.setDefaultTargetUrl("http://localhost:8003/realms/QEats/protocol/openid-connect/logout");
//        successHandler.setPostLogoutRedirectUri(postLogoutRedirectUri);
//
//        return successHandler;
//    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Example using BCryptPasswordEncoder
    }


//    @Bean
//    public UserDetailsService defaultUser() throws Exception {
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

//         Use String serialization for the keys
        template.setKeySerializer(new StringRedisSerializer());

        // Use generic serialization for the values
        template.setValueSerializer(new StringRedisSerializer());

        // Set up the hash key and value serialization (for hash operations)
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.setEnableTransactionSupport(true);

        template.afterPropertiesSet();
        return template;
    }


}
