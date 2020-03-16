package pl.kania.warehousemanager.services.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenCreatorTest {

    @ParameterizedTest
    @MethodSource(value = "pl.kania.warehousemanager.services.security.jwt.TestTokenFactory#getTestDataRequiredToCreateToken")
    void createNewTokenThenCheckIfAllValuesAreValidAndClaimsArePresent(User user, ClientDetails client, String issuer, String audience) {
        String token = TokenCreator.createJWT(user, client, issuer, audience);
        DecodedJWT decodedToken = JWT.decode(token);

        assertAll(
                () -> assertEquals(decodedToken.getSubject(), user.getId().toString(), "Different subject"),
                () -> assertEquals(decodedToken.getIssuer(), issuer, "Different issuer"),
                () -> {
                    List<String> audienceFromToken = decodedToken.getAudience();
                    assertTrue(audienceFromToken.contains(audience), "Lack of audience: " + audience);
                    assertTrue(audienceFromToken.contains(client.getClientId()), "Lack of audience: " + client.getClientId());
                },
                () -> {
                    Claim login = decodedToken.getClaim("login");
                    assertNotNull(login, "Lack of login claim");
                    assertEquals(login.asString(), user.getLogin(), "Different login claim");
                },
                () -> {
                    Claim role = decodedToken.getClaim("role");
                    assertNotNull(role, "Lack of role claim");
                    assertEquals(role.asString(), user.getRole().name(), "Different role");
                },
                () -> {
                    Claim clientId = decodedToken.getClaim("clientId");
                    assertNotNull(clientId, "Lack of clientId claim");
                    assertEquals(clientId.asString(), client.getClientId(), "Different clientId");
                },
                () -> assertEquals(decodedToken.getAlgorithm(), Algorithm.HMAC256("").getName()),
                () -> {
                    LocalDate now = LocalDate.now();
                    LocalDate nowPlus6days = now.plusDays(6);
                    LocalDate nowPlus8days = now.plusDays(8);

                    Date expiresAtDate = decodedToken.getExpiresAt();
                    assertNotNull(expiresAtDate, "Lack of expiresAt date");

                    LocalDate expiresAt = new java.sql.Date(expiresAtDate.getTime()).toLocalDate();
                    assertFalse(nowPlus6days.isAfter(expiresAt), "Token expires too soon - after 6 days should be still valid");
                    assertTrue(nowPlus8days.isAfter(expiresAt), "Token expires too late - after 8 days should be expired");
                }
        );
    }
}