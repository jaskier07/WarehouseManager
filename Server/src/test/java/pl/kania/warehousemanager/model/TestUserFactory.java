package pl.kania.warehousemanager.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.kania.warehousemanager.model.db.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUserFactory {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static User getUserEmployee() {
        User user = new User("userEmployee", passwordEncoder.encode("test-password"), WarehouseRole.EMPLOYEE);
        user.setId(1L);
        return user;
    }

    public static User getUserManager() {
        User user = new User ("userManager", passwordEncoder.encode("test-password"), WarehouseRole.MANAGER);
        user.setId(2L);
        return user;
    }

    public static User getUserWithRole(WarehouseRole role) {
        if (role == WarehouseRole.EMPLOYEE) {
            return getUserEmployee();
        } else if (role == WarehouseRole.MANAGER) {
            return  getUserManager();
        }
        throw new IllegalArgumentException("Uknown role");
    }
}
