//package dev.qeats.auth_service.service;
//
//import dev.qeats.auth_service.AuthServiceApplication;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
////@SpringBootTest(classes = AuthServiceApplication.class)
//@ContextConfiguration(classes = AuthServiceApplication.class)
//public class JwtServiceTest {
//
//    private JwtService jwtService;
//
//    @Value("${jwt.secret-key}")
//    private String secretKey;
//
//    @BeforeEach
//    public void setUp() {
//        jwtService = new JwtService();
//        jwtService.secretKey = "ioihIOHuidfhyuiYbudOhdTdjInTnPanuyunBhysuUbsoJbSDaubsdifuUZBdiszuBDUIebs";  // Inject the secret key
//        jwtService.init();  // Initialize the key
//    }
//
//    @Test
//    public void testForJwtSigningProperly() {
//        String token = jwtService.generateToken("testUser");
//        System.out.println(token);
//        assertNotNull(token);
//    }
//
//    @Test
//    public void testForJwtUserNameIfTokenIsValid() {
//        String token = jwtService.generateToken("testUser");
//        String username = jwtService.getUsername(token);
//        assertEquals("testUser", username);
//    }
//
//
//    @Test
//    public void testForJwtUserNameIfTokenIsExpiredShouldThrowError() {
//        // Creating an expired token
//        String expiredToken = Jwts.builder()
//                .setIssuer("auth-service")
//                .setSubject("testUser")
//                .setIssuedAt(new Date(System.currentTimeMillis() - 1000)) // Set past issuedAt time
//                .setExpiration(new Date(System.currentTimeMillis() - 500)) // Set past expiration time
//                .signWith(jwtService.key)
//                .compact();
//
//        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
//            jwtService.getUsername(expiredToken);
//        });
//    }
//
//    @Test
//    public void testForJwtGetAllClaims() {
//        String token = jwtService.generateToken("testUser");
//        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtService.key).build().parseClaimsJws(token);
//        assertEquals("testUser", claims.getBody().getSubject());
//        assertEquals("auth-service", claims.getBody().getIssuer());
//    }
//
//    @Test
//    public void testForJwtValidateTokenShouldReturnTrueIfValid() {
//        String token = jwtService.generateToken("testUser");
//        assertTrue(jwtService.validateToken(token));
//    }
//
//    @Test
//    public void testForJwtValidateTokenShouldReturnFalseIfNotValid() {
//        String invalidToken = "invalid.token.value";
//        assertFalse(jwtService.validateToken(invalidToken));
//    }
//
//}
