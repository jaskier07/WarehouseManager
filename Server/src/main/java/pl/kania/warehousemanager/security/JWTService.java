package pl.kania.warehousemanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.kania.warehousemanager.model.OauthClientDetails;
import pl.kania.warehousemanager.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JWTService {

    public static String createJwt(User user, OauthClientDetails clientDetails, String issuer, String audience) {
        return JWT.create()
                .withIssuer(issuer)
                .withAudience(clientDetails.getClientId(), audience)
                .withExpiresAt(Date.from(LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.UTC)))
                .withClaim("name", user.getLogin())
                .withClaim("role", user.getRole().name())
                .withClaim("clientId", clientDetails.getClientId())
                .sign(Algorithm.HMAC256(clientDetails.getClientSecret()));
    }

    public static DecodedJWT decodeToken(String jwt) {
        DecodedJWT decodedJwt = JWT.decode(jwt);
        return decodedJwt;
    }
}
