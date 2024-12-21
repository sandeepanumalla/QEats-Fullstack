package dev.qeats.auth_service;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(
		name = "security_auth",
		description = "user default user and password is user:password",
		type = SecuritySchemeType.OPENIDCONNECT,
		scheme = "bearer",
		in = SecuritySchemeIn.HEADER,
		openIdConnectUrl = "http://localhost:8003/realms/QEats/.well-known/openid-configuration",
		flows = @OAuthFlows(
				authorizationCode = @OAuthFlow(
						authorizationUrl = "${spring.security.oauth2.client.provider.keycloak.authorizationUri}",
						tokenUrl = "${spring.security.oauth2.client.provider.keycloak.tokenUri}",
						scopes = {
								@OAuthScope(name = "openid", description = "openid scope"),
								@OAuthScope(name = "refreshtoken", description = "refreshtoken scope")
						}
				)
		)
)
//@ComponentScan(basePackages = { "dev.qeats.auth_service"})
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
