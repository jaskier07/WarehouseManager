package pl.kania.warehousemanager.security;

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
import pl.kania.warehousemanager.beans.HeaderExtractor;
import pl.kania.warehousemanager.dao.ClientDetailsRepository;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
public class JWTService {

    @Autowired
    private Environment environment;

    @Autowired
    private ClientDetailsRepository clientDetailsRepository;

    @Autowired
    private HeaderExtractor headerExtractor;

    public String createJwt(User user, ClientDetails clientDetails) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withIssuer(environment.getProperty("server.issuer"))
                .withAudience(clientDetails.getClientId(), environment.getProperty("server.audience"))
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

    public Optional<GoogleIdToken.Payload> verifyGoogleToken(String token) {

        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    .setAudience(Collections.singleton(environment.getProperty("google.app.id")))
                    .setIssuer(environment.getProperty("google.issuer"))
//                    .setClock(Clock.SYSTEM)
                    .build();
            final GoogleIdToken verify = verifier.verify(token);
            if (verify == null) {
                return Optional.empty();
            }
            return Optional.of(verify.getPayload());
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error connecting to google verificator", e);
            return Optional.empty();
        }
    }
}
