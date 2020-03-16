package pl.kania.warehousemanager.services.security.jwt;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.services.ActionResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
class GoogleTokenVerificator {

    private Environment environment;

    GoogleTokenVerificator(@Autowired Environment environment) {
        this.environment = environment;
    }

    ActionResult<GoogleIdToken.Payload> verifyGoogleToken(String token) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    .setAudience(Collections.singleton(environment.getProperty("google.app.id")))
                    .setIssuer(environment.getProperty("google.issuer"))
                    .build();
            final GoogleIdToken verify = verifier.verify(token);
            if (verify == null) {
                return new ActionResult<>("Provided Google token is not valid");
            }
            return new ActionResult<>(verify.getPayload());
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error connecting to google verificator", e);
            return new ActionResult<>("Error connecting to google verificator");
        }
    }
}
