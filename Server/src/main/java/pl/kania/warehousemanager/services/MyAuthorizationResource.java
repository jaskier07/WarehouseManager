package pl.kania.warehousemanager.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kania.warehousemanager.Strings;
import pl.kania.warehousemanager.dao.UserRepository;
import pl.kania.warehousemanager.model.User;

import javax.ws.rs.FormParam;

@RestController()
public class MyAuthorizationResource {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    @PostMapping("/login")
    public ResponseEntity<Void> addProduct(@FormParam("username") String username, @FormParam("password") String password) {
        if (Strings.isNullOrEmpty("username") || Strings.isNullOrEmpty("password")) {
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findByLogin(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(bCryptPasswordEncoder.encode(password))) {
            return ResponseEntity.badRequest().build();
        }

        Algorithm algorithm = Algorithm.HMAC256("eloelo");
        JWT.create()
        .withIssuer("");

        // TODO utwórz token i zwróć

        return ResponseEntity.ok().build();
    }
}
