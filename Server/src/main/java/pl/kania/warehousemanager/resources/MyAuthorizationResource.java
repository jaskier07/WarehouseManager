package pl.kania.warehousemanager.resources;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kania.warehousemanager.Strings;
import pl.kania.warehousemanager.beans.HeaderExtractor;
import pl.kania.warehousemanager.dao.ClientDetailsRepository;
import pl.kania.warehousemanager.dao.UserRepository;
import pl.kania.warehousemanager.model.UserCredentials;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;
import pl.kania.warehousemanager.model.dto.LoginResult;
import pl.kania.warehousemanager.security.JWTService;

import java.util.Map;
import java.util.Optional;

@RestController
public class MyAuthorizationResource {

    @Autowired
    private Environment environment;

    @Autowired
    private ClientDetailsRepository clientRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private HeaderExtractor credentialExtractor;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResult> logIn(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        UserFromClient userFromClient = getUserFromClient(authorization, body, true);
        if (userFromClient.error != null) {
            return userFromClient.getError();
        }

        String token = createToken(userFromClient);
        return ResponseEntity.ok(new LoginResult(token, userFromClient.getUser().getLogin()));
    }

    private String createToken(UserFromClient userFromClient) {
        return jwtService.createJwt(userFromClient.getUser(), userFromClient.getClientDetails(),
                environment.getProperty("server.issuer"), environment.getProperty("server.audience"));
    }

    @PostMapping(value = "/login-with-google")
    public ResponseEntity<LoginResult> logInWithGoogle(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        UserFromClient userFromClient = getUserFromClient(authorization, body, false);

        String token = createToken(userFromClient);
        return ResponseEntity.ok(new LoginResult(token, userFromClient.getUser().getLogin()));
    }

    private UserFromClient getUserFromClient(String authorization, Map<String, String> body, boolean validatePassword) {
        final Optional<UserCredentials> credentials = credentialExtractor.extractCredentialsFromAuthorizationHeader(authorization);
        if (!credentials.isPresent()) {
            return new UserFromClient(getBadRequestResponseEntity("Provide credentials in proper format"));
        }
        final String clientId = body.get("clientId");
        final String clientSecret = body.get("clientSecret");

        if (emptyClientDetails(clientId, clientSecret)) {
            return new UserFromClient(getBadRequestResponseEntity("Provide client details"));
        }

        ClientDetails client = clientRepository.findByClientId(clientId);
        if (client == null) {
            return new UserFromClient(getBadRequestResponseEntity("Unknown client"));
        }
        if (!passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            return new UserFromClient(getBadRequestResponseEntity("Bad client details"));
        }

        User user = userRepository.findByLogin(credentials.get().getLogin());
        if (user == null) {
            return new UserFromClient(ResponseEntity.notFound().build());
        }
        if (validatePassword) {
            if (!passwordEncoder.matches(credentials.get().getPassword(),user.getPassword())) {
                return new UserFromClient(getBadRequestResponseEntity("Login and password do not match"));
            }
        }

        return new UserFromClient(user, client);
    }

    private boolean emptyClientDetails(String clientId, String clientSecret) {
        return Strings.isNullOrEmpty(clientId) || Strings.isNullOrEmpty(clientSecret);
    }

    private ResponseEntity<LoginResult> getBadRequestResponseEntity(String errorMessage) {
        final LoginResult loginResult = new LoginResult();
        loginResult.setErrorMessage(errorMessage);
        return ResponseEntity.badRequest().body(loginResult);
    }

    @Data
    private class UserFromClient {
        private User user;
        private ClientDetails clientDetails;
        private ResponseEntity<LoginResult> error;

        UserFromClient(ResponseEntity<LoginResult> error) {
            this.error = error;
        }

        UserFromClient(User user,ClientDetails clientDetails) {
            this.user = user;
            this.clientDetails = clientDetails;
        }
    }
}
