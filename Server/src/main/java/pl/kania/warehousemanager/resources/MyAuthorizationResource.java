package pl.kania.warehousemanager.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kania.warehousemanager.Strings;
import pl.kania.warehousemanager.dao.ClientDetailsRepository;
import pl.kania.warehousemanager.dao.UserRepository;
import pl.kania.warehousemanager.model.OauthClientDetails;
import pl.kania.warehousemanager.model.User;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.security.JWTService;

import javax.annotation.PostConstruct;

@RestController
public class MyAuthorizationResource {

    @Autowired
    private Environment environment;

    @Autowired
    private ClientDetailsRepository clientRepository;

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    private void fillDatabase() {
        // TODO szyfrowanie hasla
        User user = new User(null, "user", "user", WarehouseRole.EMPLOYEE);
        userRepository.save(user);
        User manager = new User(null, "admin", "admin", WarehouseRole.MANAGER);
        userRepository.save(manager);
        OauthClientDetails oauthClientToken = new OauthClientDetails();
        oauthClientToken.setClientId("androidapp");
        oauthClientToken.setClientSecret("password");
        oauthClientToken.setScope("read");
        oauthClientToken.setAuthorizedGrantTypes("password,authorization_code");
        oauthClientToken.setWebServerRedirectUri("https://www.getpostman.com/oauth2/callback");
        oauthClientToken.setAuthorities("USER");
        oauthClientToken.setAccessTokenValidity(10800);
        oauthClientToken.setRefreshTokenValidity(2592000);
        clientRepository.save(oauthClientToken);
    }


    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestParam("username") String username, @RequestParam("password") String password,
                                             @RequestParam("client_id") String clientId, @RequestParam("client_secret") String clientSecret) {
        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
            return ResponseEntity.badRequest().body("Provide credentials");
        }
        if (Strings.isNullOrEmpty(clientId) || Strings.isNullOrEmpty(clientSecret)) {
            return ResponseEntity.badRequest().body("Provide client details");
        }

        OauthClientDetails client = clientRepository.findByClientId(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body("Unknown client");
        }
        if (!client.getClientSecret().equals(clientSecret)) { // TODO szyfryl


            return ResponseEntity.badRequest().body("Bad client details");
        }

        User user = userRepository.findByLogin(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(password)) { // TODO szyfry
            return ResponseEntity.badRequest().build();
        }
        String token = JWTService.createJwt(user, client, environment.getProperty("server.issuer"), environment.getProperty("server.audience"));
        // TODO utwórz token i zwróć

        return ResponseEntity.ok(token);
    }

    @PostMapping("/oauth/authorization")
    public ResponseEntity<String> s() {
        return null;
    }
}
