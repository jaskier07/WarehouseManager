package pl.kania.warehousemanager.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;
import pl.kania.warehousemanager.services.beans.HeaderExtractor;

import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientIdExtractor {

    public static Optional<String> extractFromHeader(String header) {
        try {
            final Optional<String> token = HeaderExtractor.extractTokenFromAuthorizationHeader(header);
            if (!token.isPresent()) {
                return Optional.empty();
            }
            final DecodedJWT decodedJwt = JWT.decode(token.get());

            final Claim clientIdClaim = decodedJwt.getClaim("clientId");
            if (clientIdClaim.isNull()) {
                return Optional.empty();
            }
            return Optional.of(clientIdClaim.asString());
        } catch (TokenNotFoundInHeaderException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

}
