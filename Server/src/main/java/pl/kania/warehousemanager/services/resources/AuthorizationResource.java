package pl.kania.warehousemanager.services.resources;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kania.warehousemanager.Strings;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;
import pl.kania.warehousemanager.model.UserCredentials;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;
import pl.kania.warehousemanager.model.dto.LoginResult;
import pl.kania.warehousemanager.services.ActionResult;
import pl.kania.warehousemanager.services.beans.HeaderExtractor;
import pl.kania.warehousemanager.services.dao.ClientDetailsRepository;
import pl.kania.warehousemanager.services.dao.UserRepository;
import pl.kania.warehousemanager.services.security.jwt.JWTService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
public class AuthorizationResource {

    @Autowired
    private Environment environment;

    @Autowired
    private ClientDetailsRepository clientRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping(value = "/sign-in-with-google")
    public ResponseEntity<LoginResult> signInWithGoogle(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        final List<String> errors = new ArrayList<>();
        try {
            final Optional<String> googleToken = HeaderExtractor.extractTokenFromAuthorizationHeader(authorization);
            if (!googleToken.isPresent()) {
                return getErrorRequest(errors);
            }

            final ActionResult<GoogleIdToken.Payload> payload = jwtService.getPayloadFromGoogleToken(googleToken.get());
            if (payload.isError()) {
                return getErrorRequest(payload.getErrorMessage());
            }

            final Optional<ClientDetails> clientFromRequest = getClientFromRequest(body, errors);
            if (!clientFromRequest.isPresent()) {
                return getErrorRequest(errors);
            }

            if (userRepository.findByLogin(payload.getResult().getEmail()) != null) {
                errors.add("User with this login already exists");
                return getErrorRequest(errors);
            }

            final User userToSave = new User(payload.getResult().getEmail(), null, WarehouseRole.EMPLOYEE);
            final User savedUser = userRepository.save(userToSave);

            final String jwt = jwtService.createJwt(savedUser, clientFromRequest.get());
            return ResponseEntity.ok(new LoginResult(jwt, savedUser.getLogin(), savedUser.isManager()));
        } catch (TokenNotFoundInHeaderException e) {
            log.error(e.getMessage());
            return getErrorRequest(e.getMessage());
        }
    }

    @PostMapping(value = "/log-in-with-google")
    public ResponseEntity<LoginResult> logInWithGoogle(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        final List<String> errors = new ArrayList<>();
        try {
            final Optional<String> googleToken = HeaderExtractor.extractTokenFromAuthorizationHeader(authorization);
            if (!googleToken.isPresent()) {
                return getErrorRequest(errors);
            }

            final ActionResult<GoogleIdToken.Payload> payload = jwtService.getPayloadFromGoogleToken(googleToken.get());
            if (payload.isError()) {
                return getErrorRequest(payload.getErrorMessage());
            }

            final Optional<ClientDetails> clientFromRequest = getClientFromRequest(body, errors);
            if (!clientFromRequest.isPresent()) {
                return getErrorRequest(errors);
            }

            final User user = userRepository.findByLogin(payload.getResult().getEmail());
            if (user == null) {
                errors.add("User not found");
                return getErrorRequest(errors);
            }

            final String jwt = jwtService.createJwt(user, clientFromRequest.get());

            return ResponseEntity.ok(new LoginResult(jwt, user.getLogin(), user.isManager()));
        } catch (TokenNotFoundInHeaderException e) {
            log.error(e.getMessage());
            return getErrorRequest(e.getMessage());
        }
    }

    @PostMapping(value = "/log-in")
    public ResponseEntity<LoginResult> logIn(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        final List<String> errors = new ArrayList<>();
        Optional<UserAndClientFromRequest> userFromClient = getUserFromClient(authorization, body, errors, true);
        if (!userFromClient.isPresent()) {
            return getErrorRequest(errors);
        }

        final String token = createToken(userFromClient.get());
        return ResponseEntity.ok(new LoginResult(token, userFromClient.get().getUser().getLogin(), userFromClient.get().getUser().isManager()));
    }

    @PostMapping(value = "/sign-in")
    public ResponseEntity<LoginResult> signIn(@RequestHeader("Authorization") String authorization, @RequestParam Map<String, String> body) {
        try {
            final List<String> errors = new ArrayList<>();
            Optional<UserCredentials> credentials;
            credentials = HeaderExtractor.extractCredentialsFromAuthorizationHeader(authorization, errors);
            if (!credentials.isPresent()) {
                return getErrorRequest(errors);
            }

            final Optional<ClientDetails> clientFromRequest = getClientFromRequest(body, errors);
            if (!clientFromRequest.isPresent()) {
                return getErrorRequest(errors);
            }

            if (userRepository.findByLogin(credentials.get().getLogin()) != null) {
                errors.add("User with this login already exists");
                return getErrorRequest(errors);
            }

            final User userToSave = new User(credentials.get().getLogin(), passwordEncoder.encode(credentials.get().getPassword()), WarehouseRole.EMPLOYEE);
            final User savedUser = userRepository.save(userToSave);

            final String token = createToken(new UserAndClientFromRequest(savedUser, clientFromRequest.get()));
            return ResponseEntity.ok(new LoginResult(token, savedUser.getLogin(), savedUser.isManager()));
        } catch (TokenNotFoundInHeaderException e) {
            log.error("Token not found in header", e);
            return getErrorRequest(e.getMessage());
        }
    }

    private String createToken(UserAndClientFromRequest userFromClient) {
        return jwtService.createJwt(userFromClient.getUser(), userFromClient.getClientDetails());
    }

    private Optional<UserAndClientFromRequest> getUserFromClient(String authorization, Map<String, String> body, List<String> errors, boolean validatePassword) {
        final Optional<UserCredentials> credentials;
        try {
            credentials = HeaderExtractor.extractCredentialsFromAuthorizationHeader(authorization, errors);
            if (!credentials.isPresent()) {
                return Optional.empty();
            }
        } catch (TokenNotFoundInHeaderException e) {
            log.error("Token not found in authorization header", e);
            return Optional.empty();
        }

        Optional<ClientDetails> clientFromRequest = getClientFromRequest(body, errors);
        if (!clientFromRequest.isPresent()) {
            return Optional.empty();
        }

        User user = userRepository.findByLogin(credentials.get().getLogin());
        if (user == null) {
            errors.add("User not found");
            return Optional.empty();
        }
        if (validatePassword) {
            if (!passwordEncoder.matches(credentials.get().getPassword(), user.getPassword())) {
                errors.add("Login and password do not match");
                return Optional.empty();
            }
        }

        return Optional.of(new UserAndClientFromRequest(user, clientFromRequest.get()));
    }

    private Optional<ClientDetails> getClientFromRequest(Map<String, String> body, List<String> errors) {
        final String clientId = body.get("clientId");
        final String clientSecret = body.get("clientSecret");

        if (emptyClientDetails(clientId, clientSecret)) {
            errors.add("Empty client credentials");
            return Optional.empty();
        }

        ClientDetails client = clientRepository.findByClientId(clientId);
        if (client == null) {
            errors.add("Client not found");
            return Optional.empty();
        }
        if (!passwordEncoder.matches(clientSecret, client.getClientSecret())) {
            errors.add("Bad client credentials");
            return Optional.empty();
        }

        return Optional.of(client);
    }

    private boolean emptyClientDetails(String clientId, String clientSecret) {
        return Strings.isNullOrEmpty(clientId) || Strings.isNullOrEmpty(clientSecret);
    }

    private ResponseEntity<LoginResult> getErrorRequest(List<String> errorMessage) {
        final LoginResult loginResult = new LoginResult();
        loginResult.setErrorMessage(errorMessage.size() == 1 ? errorMessage.get(0) : "Operation failed");
        return ResponseEntity.badRequest().body(loginResult);
    }

    private ResponseEntity<LoginResult> getErrorRequest(String errorMessage) {
        return getErrorRequest(Collections.singletonList(errorMessage));
    }

    @Value
    private static class UserAndClientFromRequest {
        private User user;
        private ClientDetails clientDetails;
    }
}
