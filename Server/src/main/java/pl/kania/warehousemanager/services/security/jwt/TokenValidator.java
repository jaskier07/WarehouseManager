package pl.kania.warehousemanager.services.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.services.ActionResult;

@Service
class TokenValidator {

    private Environment environment;

    public TokenValidator(@Autowired Environment environment) {
        this.environment = environment;
    }

    ActionResult<DecodedJWT> validateIntegrity(DecodedJWT token, String clientSecret) {
        try {
            return new ActionResult<>(JWT.require(Algorithm.HMAC256(clientSecret))
                    .withIssuer(environment.getProperty("server.issuer"))
                    .withAudience(environment.getProperty("server.audience"))
                    .build()
                    .verify(token));
        } catch (AlgorithmMismatchException ame) {
            return new ActionResult<>("Wrong alghoritm.");
        } catch (SignatureVerificationException sve) {
           return new ActionResult<>("Invalid signature.");
        } catch (TokenExpiredException tee) {
            return new ActionResult<>("Token expired.");
        } catch (InvalidClaimException ice) {
            return new ActionResult<>("Invalid claim.");
        }
    }
}
