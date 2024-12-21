package dev.qeats.api_gateway;

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
		type = SecuritySchemeType.OAUTH2,
		scheme = "bearer",
		in = SecuritySchemeIn.HEADER,
		openIdConnectUrl = "http://localhost:8002/realms/task-management/.well-known/openid-configuration",
		flows = @OAuthFlows(
				authorizationCode = @OAuthFlow(
						authorizationUrl = "${openapi.oauthFlow.authorizationUrl}",
						tokenUrl = "${openapi.oauthFlow.tokenUrl}",
						scopes = {
								@OAuthScope(name = "openid", description = "openid scope")
						}
				)
		)
)
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
