package pl.kania.warehousemanager.services;

import org.junit.jupiter.params.provider.Arguments;
import pl.kania.warehousemanager.model.TestClientDetailsFactory;
import pl.kania.warehousemanager.model.TestUserFactory;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.services.security.TokenCreator;

import java.util.stream.Stream;

public class TestHeaderFactory {

    public static String getValidHeaderWithUserRole(WarehouseRole role) {
        return "BEARER " + getValidTokenWithUserRole(role);
    }

    public static String getValidTokenWithUserRole(WarehouseRole role) {
        return TokenCreator.createJWT(TestUserFactory.getUserWithRole(role), TestClientDetailsFactory.getClientDetails(), "test-issuer", "test-audience");
    }

    public static Stream<Arguments> getValidHeaders() {
        String tokenManager = getValidTokenWithUserRole(WarehouseRole.MANAGER);
        String tokenEmployee = getValidTokenWithUserRole(WarehouseRole.EMPLOYEE);

        Stream<Arguments> streamManager = getValidHeaderPrefixes()
                .map(h -> Arguments.of(h + tokenManager, tokenManager));
        Stream<Arguments> streamEmployee = getValidHeaderPrefixes()
                .map(h -> Arguments.of(h + tokenEmployee, tokenEmployee));
        return Stream.concat(streamManager, streamEmployee);
}

    public static Stream<String> getValidHeaderPrefixes() {
        return Stream.of(
                "BEARER ",
                "Bearer ",
                "bearer "
        );
    }
}
