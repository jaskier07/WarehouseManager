package pl.kania.warehousemanager.services.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.db.User;
import pl.kania.warehousemanager.services.dao.ClientDetailsRepository;
import pl.kania.warehousemanager.services.dao.ProductRepository;
import pl.kania.warehousemanager.services.dao.UserRepository;

import java.sql.Timestamp;
import java.util.Date;

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

//    @Autowired
//    private VectorClockService vectorClockService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final User user = new User("user", passwordEncoder.encode(environment.getProperty("test.user.password")), WarehouseRole.EMPLOYEE);
        userRepository.save(user);
        final User manager = new User("A", passwordEncoder.encode(environment.getProperty("test.manager.password")), WarehouseRole.MANAGER);
        userRepository.save(manager);

        final ClientDetails oauthClientToken = new ClientDetails();
        oauthClientToken.setClientId(environment.getProperty("android.client.id"));
        oauthClientToken.setClientSecret(passwordEncoder.encode(environment.getProperty("android.client.secret")));
        clientRepository.save(oauthClientToken);

        final ClientDetails oauthClientToken2 = new ClientDetails();
        oauthClientToken2.setClientId(environment.getProperty("android.client2.id"));
        oauthClientToken2.setClientSecret(passwordEncoder.encode(environment.getProperty("android.client2.secret")));
        clientRepository.save(oauthClientToken2);

        final Product samsung = new Product();
        samsung.setManufacturerName("Samsung");
        samsung.setModelName("Galaxy s8");
        samsung.setPrice(19.90D);
        samsung.setQuantity(5);
        samsung.setLastModified(Timestamp.from(new Date().toInstant()));
//        samsung.setVectorClock(vectorClockService.createNewVector(5));
        productRepository.save(samsung);

        final Product iphone = new Product();
        iphone.setManufacturerName("Apple");
        iphone.setModelName("X");
        iphone.setQuantity(23);
        iphone.setPrice(9999.9D);
        iphone.setLastModified(Timestamp.from(new Date().toInstant()));

//        iphone.setVectorClock(vectorClockService.createNewVector(23));
        productRepository.save(iphone);
    }
}
