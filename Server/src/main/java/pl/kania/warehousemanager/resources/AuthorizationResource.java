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
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;
import pl.kania.warehousemanager.model.dto.LoginResult;
import pl.kania.warehousemanager.security.JWTService;

import java.util.Map;
import java.util.Optional;

@RestController
public class AuthorizationResource {

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

    @PostMapping(value = "/login-with-google")
    public ResponseEntity<LoginResult> logInWithGoogle(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        UserAndClientFromRequest userFromClient = getUserFromClient(authorization, body, false);

        final String token = createToken(userFromClient);
        return ResponseEntity.ok(new LoginResult(token, userFromClient.getUser().getLogin()));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResult> logIn(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        UserAndClientFromRequest userFromClient = getUserFromClient(authorization, body, true);
        if (userFromClient.error != null) {
            return userFromClient.getError();
        }

        final String token = createToken(userFromClient);
        return ResponseEntity.ok(new LoginResult(token, userFromClient.getUser().getLogin()));
    }

    @PostMapping(value = "/sign-in")
    public ResponseEntity<LoginResult> signIn(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        final Optional<UserCredentials> credentials = credentialExtractor.extractCredentialsFromAuthorizationHeader(authorization);
        if (!credentials.isPresent()) {
            return getBadRequestResponseEntity("Provide credentials in proper format");
        }

        final ClientFromRequest clientFromRequest = getClientFromRequest(body);
        if (clientFromRequest.error != null) {
            return clientFromRequest.getError();
        }

        if (userRepository.findByLogin(credentials.get().getLogin()) != null) {
            return getBadRequestResponseEntity("User with this login already exists");
        }

        final User userToSave = new User(null, credentials.get().getLogin(), passwordEncoder.encode(credentials.get().getPassword()), WarehouseRole.EMPLOYEE);
        final User savedUser = userRepository.save(userToSave);

        final String token = createToken(new UserAndClientFromRequest(savedUser, clientFromRequest.getClientDetails()));
        return ResponseEntity.ok(new LoginResult(token, savedUser.getLogin()));
    }

    private String createToken(UserAndClientFromRequest userFromClient) {
        return jwtService.createJwt(userFromClient.getUser(), userFromClient.getClientDetails(),
                environment.getProperty("server.issuer"), environment.getProperty("server.audience"));
    }

    private UserAndClientFromRequest getUserFromClient(String authorization, Map<String, String> body, boolean validatePassword) {
        final Optional<UserCredentials> credentials = credentialExtractor.extractCredentialsFromAuthorizationHeader(authorization);
        if (!credentials.isPresent()) {
            return new UserAndClientFromRequest(getBadRequestResponseEntity("Provide credentials in proper format"));
        }

        ClientFromRequest clientFromRequest = getClientFromRequest(body);
        if (clientFromRequest.error != null) {
            return new UserAndClientFromRequest(clientFromRequest.getError());
        }

        User user = userRepository.findByLogin(credentials.get().getLogin());
        if (user == null) {
            return new UserAndClientFromRequest(ResponseEntity.notFound().build());
        }
        if (validatePassword) {
            if (!passwordEncoder.matches(credentials.get().getPassword(), user.getPassword())) {
                return new UserAndClientFromRequest(getBadRequestResponseEntity("Login and password do not match"));
            }
        }

        return new UserAndClientFromRequest(user, clientFromRequest.getClientDetails());
    }

    private ClientFromRequest getClientFromRequest(Map<String, String> body) {
        final String clientId = body.get("clientId");
        final String clientSecret = body.get("clientSecret");

        if (emptyClientDetails(clientId, clientSecret)) {
            return new ClientFromRequest(getBadRequestResponseEntity("Provide client details"));
        }

        ClientDetails client = clientRepository.findByClientId(clientId);
        if (client == null) {
            return new ClientFromRequest(getBadRequestResponseEntity("Unknown client"));
        }
        if (!passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            return new ClientFromRequest(getBadRequestResponseEntity("Bad client details"));
        }

        return new ClientFromRequest(client);
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
    private static class ClientFromRequest {
        private ClientDetails clientDetails;
        private ResponseEntity<LoginResult> error;

        ClientFromRequest(ClientDetails clientDetails) {
            this.clientDetails = clientDetails;
        }

        ClientFromRequest(ResponseEntity<LoginResult> error) {
            this.error = error;
        }
    }

    @Data
    private static class UserAndClientFromRequest {
        private User user;
        private ClientDetails clientDetails;
        private ResponseEntity<LoginResult> error;

        UserAndClientFromRequest(ResponseEntity<LoginResult> error) {
            this.error = error;
        }

        UserAndClientFromRequest(User user, ClientDetails clientDetails) {
            this.user = user;
            this.clientDetails = clientDetails;
        }
    }
}
