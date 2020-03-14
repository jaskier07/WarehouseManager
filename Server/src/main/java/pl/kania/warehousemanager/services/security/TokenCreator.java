package pl.kania.warehousemanager.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenCreator {
    public static String createJWT(User user, ClientDetails clientDetails, String issuer, String audience) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withIssuer(issuer)
                .withAudience(clientDetails.getClientId(), audience)
                .withExpiresAt(Date.from(LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.UTC)))
                .withClaim("login", user.getLogin())
                .withClaim("role", user.getRole().name())
                .withClaim("clientId", clientDetails.getClientId())
                .sign(Algorithm.HMAC256(clientDetails.getClientSecret()));
    }
}
