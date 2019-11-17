package pl.kania.warehousemanager.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kania.warehousemanager.dao.ClientDetailsRepository;
import pl.kania.warehousemanager.dao.ProductRepository;
import pl.kania.warehousemanager.dao.UserRepository;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.db.User;

@Component
public class InitialDataLoader implements ApplicationRunner {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private Environment environment;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientDetailsRepository clientRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User(null, "user", passwordEncoder.encode(environment.getProperty("test.user.password")), WarehouseRole.EMPLOYEE);
        userRepository.save(user);
        User manager = new User(null, "admin", passwordEncoder.encode(environment.getProperty("test.manager.password")), WarehouseRole.MANAGER);
        userRepository.save(manager);

        ClientDetails oauthClientToken = new ClientDetails();
        oauthClientToken.setClientId(environment.getProperty("android.client.id"));
        oauthClientToken.setClientSecret(passwordEncoder.encode(environment.getProperty("android.client.secret")));
        oauthClientToken.setScope("read");
        oauthClientToken.setAuthorizedGrantTypes("password,authorization_code");
        oauthClientToken.setWebServerRedirectUri("https://www.getpostman.com/oauth2/callback");
        oauthClientToken.setAuthorities("USER");
        oauthClientToken.setAccessTokenValidity(10800);
        oauthClientToken.setRefreshTokenValidity(2592000);
        clientRepository.save(oauthClientToken);

        Product samsung = new Product();
        samsung.setManufacturerName("Samsung");
        samsung.setModelName("Galaxy s8");
        samsung.setPrice(19.90D);
        samsung.setQuantity(5);
        productRepository.save(samsung);

        Product iphone = new Product();
        iphone.setManufacturerName("Apple");
        iphone.setModelName("X");
        iphone.setQuantity(23);
        iphone.setPrice(9999.9D);
        productRepository.save(iphone);
    }
}
