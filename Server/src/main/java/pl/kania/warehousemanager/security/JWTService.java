package pl.kania.warehousemanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.beans.HeaderExtractor;
import pl.kania.warehousemanager.dao.ClientDetailsRepository;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class JWTService {

    @Autowired
    private Environment environment;

    @Autowired
    private ClientDetailsRepository clientDetailsRepository;

    @Autowired
    private HeaderExtractor headerExtractor;

    public String createJwt(User user, ClientDetails clientDetails, String issuer, String audience) {
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

    public boolean hasRole(WarehouseRole requiredRole, String header) {
        final Optional<String> token = headerExtractor.extractTokenFromAuthorizationHeader(header);
        if (!token.isPresent()) {
            return false;
        }
        final DecodedJWT decodedToken = JWT.decode(token.get());
        final Claim role = decodedToken.getClaim("role");
        if (role.isNull()) {
            return false;
        }
        try {
            WarehouseRole userRole = WarehouseRole.valueOf(role.asString());
            if (requiredRole == WarehouseRole.MANAGER) {
                return userRole == WarehouseRole.MANAGER;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean checkPermissions(String header, Consumer<String> responseSetter) {
        final Optional<String> token = headerExtractor.extractTokenFromAuthorizationHeader(header);
        if (!token.isPresent()) {
            responseSetter.accept("No token in authorization header");
            return false;
        }
        final DecodedJWT decodedJwt = JWT.decode(token.get());

        final Claim clientIdClaim = decodedJwt.getClaim("clientId");
        if (clientIdClaim.isNull()) {
            responseSetter.accept("Lack of client id.");
            return false;
        }

        final String clientId = clientIdClaim.asString();
        final String clientSecret = clientDetailsRepository.findClientSecretByClientId(clientId);
        if (clientSecret == null) {
            responseSetter.accept("Bad client id.");
            return false;
        }

        final DecodedJWT validatedJwt = validateIntegrity(decodedJwt, clientSecret, responseSetter);
        if (validatedJwt == null) {
            return false;
        }

        if (!decodedJwt.getIssuer().equals(environment.getProperty("server.issuer"))) {
            responseSetter.accept("Invalid issuer");
        } else if (!decodedJwt.getAudience().contains(environment.getProperty("server.audience"))) {
            responseSetter.accept("Server is not in the audience.");
        } else {
            return true;
        }
        return false;
    }

    private DecodedJWT validateIntegrity(DecodedJWT token, String clientSecret, Consumer<String> responseSetter) {
        try {
            return JWT.require(Algorithm.HMAC256(clientSecret))
                    .withIssuer(environment.getProperty("server.issuer"))
                    .withAudience(environment.getProperty("server.audience"))
                    .build()
                    .verify(token);
        } catch (AlgorithmMismatchException ame) {
            responseSetter.accept("Wrong alghoritm.");
        } catch (SignatureVerificationException sve) {
            responseSetter.accept("Invalid signature.");
        } catch (TokenExpiredException tee) {
            responseSetter.accept("Token expired.");
        } catch (InvalidClaimException ice) {
            responseSetter.accept("Invalid claim.");
        }
        return null;
    }
}
