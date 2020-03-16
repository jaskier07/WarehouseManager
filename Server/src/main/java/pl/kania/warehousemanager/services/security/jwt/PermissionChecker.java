package pl.kania.warehousemanager.services.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;
import pl.kania.warehousemanager.services.ActionResult;
import pl.kania.warehousemanager.services.beans.HeaderExtractor;
import pl.kania.warehousemanager.services.dao.ClientDetailsRepository;

import java.util.Optional;

@Slf4j
@Service
class PermissionChecker {

    private ClientDetailsRepository clientDetailsRepository;
    private Environment environment;
    private TokenValidator tokenValidator;

    PermissionChecker(@Autowired TokenValidator tokenValidator, @Autowired Environment environment, @Autowired ClientDetailsRepository clientDetailsRepository) {
        this.tokenValidator = tokenValidator;
        this.environment = environment;
        this.clientDetailsRepository = clientDetailsRepository;
    }

    ActionResult<Boolean> checkPermissions(String header) {
        try {
            final Optional<String> token = HeaderExtractor.extractTokenFromAuthorizationHeader(header);
            if (!token.isPresent()) {
                return new ActionResult<>(false, "No token in authorization header");
            }
            final DecodedJWT decodedJwt = JWT.decode(token.get());

            final Claim clientIdClaim = decodedJwt.getClaim("clientId");
            if (clientIdClaim.isNull()) {
                return new ActionResult<>(false, "Lack of client id.");
            }

            final String clientId = clientIdClaim.asString();
            final String clientSecret = clientDetailsRepository.findClientSecretByClientId(clientId);
            if (clientSecret == null) {
                return new ActionResult<>(false, "Bad client id.");
            }

            ActionResult<DecodedJWT> validatedJwt = tokenValidator.validateIntegrity(decodedJwt, clientSecret);
            if (validatedJwt.isError()) {
                return new ActionResult<>(false, validatedJwt.getErrorMessage());
            }

            if (!decodedJwt.getIssuer().equals(environment.getProperty("server.issuer"))) {
                return new ActionResult<>("Invalid issuer");
            } else if (!decodedJwt.getAudience().contains(environment.getProperty("server.audience"))) {
                return new ActionResult<>("Server is not in the audience.");
            } else {
                return new ActionResult<>(true);
            }
        } catch (TokenNotFoundInHeaderException e) {
            log.error(e.getMessage());
            return new ActionResult<>(e.getMessage());
        }
    }
}
