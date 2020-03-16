package pl.kania.warehousemanager.services.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;
import pl.kania.warehousemanager.services.ActionResult;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenValidatorTest {

    private static TokenValidator tokenValidator;

    @BeforeAll
    static void initializeEnvironmentForTokenValidator() {
        Environment environment = mock(Environment.class);
        when(environment.getProperty("server.issuer")).thenReturn("test-issuer");
        when(environment.getProperty("server.audience")).thenReturn("test-audience");
        tokenValidator = new TokenValidator(environment);
    }

    @ParameterizedTest
    @MethodSource(value = "pl.kania.warehousemanager.services.security.jwt.TestTokenFactory#getTestDataRequiredToCreateToken")
    void givenExpiredTokenValidateIntegrityAndReturnFalse(User user, ClientDetails client, String issuer, String audience) {
        LocalDate localDateExpiresAt = LocalDate.now().minusYears(1);
        Date mockedDate = Date.from(localDateExpiresAt
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());

        String token = JWT.create()
                .withSubject(user.getId().toString())
                .withIssuer(issuer)
                .withAudience(client.getClientId(), audience)
                .withExpiresAt(mockedDate)
                .withClaim("login", user.getLogin())
                .withClaim("role", user.getRole().name())
                .withClaim("clientId", client.getClientId())
                .sign(Algorithm.HMAC256(client.getClientSecret()));

        ActionResult<DecodedJWT> decodedJWT = tokenValidator.validateIntegrity(JWT.decode(token), client.getClientSecret());
        assertTrue(decodedJWT.isError(), "Validation result should be an error due to its expiration");
        assertEquals("Token expired.", decodedJWT.getErrorMessage(), "Invalid token error message");
    }

}