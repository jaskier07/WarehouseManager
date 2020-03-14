package pl.kania.warehousemanager.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.kania.warehousemanager.model.db.ClientDetails;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestClientDetailsFactory {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static ClientDetails getClientDetails() {
        ClientDetails client = new ClientDetails();
        client.setClientId("android-app");
        client.setClientSecret(passwordEncoder.encode("client-password"));
        client.setId(1L);
        return client;
    }

}
