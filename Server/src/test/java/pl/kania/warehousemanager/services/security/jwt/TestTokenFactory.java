package pl.kania.warehousemanager.services.security.jwt;

import org.junit.jupiter.params.provider.Arguments;
import pl.kania.warehousemanager.model.TestClientDetailsFactory;
import pl.kania.warehousemanager.model.TestUserFactory;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;

import java.util.stream.Stream;

public class TestTokenFactory {

    public static String getValidTokenWithUserRole(WarehouseRole role) {
        return TokenCreator.createJWT(TestUserFactory.getUserWithRole(role), TestClientDetailsFactory.getClientDetails(), "test-issuer", "test-audience");
    }

    public static String getValidTokenWithUserRole(WarehouseRole role, ClientDetails clientDetails) {
        return TokenCreator.createJWT(TestUserFactory.getUserWithRole(role), clientDetails, "test-issuer", "test-audience");
    }

    public static Stream<Arguments> getTestDataRequiredToCreateToken() {
        return Stream.concat(
                getTestDataRequiredToCreateTokenForRole(WarehouseRole.MANAGER),
                getTestDataRequiredToCreateTokenForRole(WarehouseRole.EMPLOYEE)
        );
    }

    private static Stream<Arguments> getTestDataRequiredToCreateTokenForRole(WarehouseRole role) {
        return Stream.of(Arguments.of(
                TestUserFactory.getUserWithRole(role),
                TestClientDetailsFactory.getClientDetails(),
                "test-issuer",
                "test-audience"));
    }
}

