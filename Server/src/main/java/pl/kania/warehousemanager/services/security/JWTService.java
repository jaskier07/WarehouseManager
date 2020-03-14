package pl.kania.warehousemanager.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;
import pl.kania.warehousemanager.services.beans.HeaderExtractor;
import pl.kania.warehousemanager.services.dao.ClientDetailsRepository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
public class JWTService {

    @Autowired
    private Environment environment;

    @Autowired
    private ClientDetailsRepository clientDetailsRepository;

    public String createJwt(User user, ClientDetails clientDetails) {
        return TokenCreator.createJWT(user, clientDetails, environment.getProperty("server.issuer"), environment.getProperty("server.audience"));
    }


    public boolean checkPermissions(String header, Consumer<String> responseSetter) {
        try {
            final Optional<String> token = HeaderExtractor.extractTokenFromAuthorizationHeader(header);
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
        } catch (TokenNotFoundInHeaderException e) {
            log.error(e.getMessage());
            return false;
        }
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

    public Optional<GoogleIdToken.Payload> verifyGoogleToken(String token, List<String> errors) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    .setAudience(Collections.singleton(environment.getProperty("google.app.id")))
                    .setIssuer(environment.getProperty("google.issuer"))
                    .build();
            final GoogleIdToken verify = verifier.verify(token);
            if (verify == null) {
                errors.add("Provided Google token is not valid");
                return Optional.empty();
            }
            return Optional.of(verify.getPayload());
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error connecting to google verificator", e);
            errors.add("Error connecting to google verificator");
            return Optional.empty();
        }
    }
}
