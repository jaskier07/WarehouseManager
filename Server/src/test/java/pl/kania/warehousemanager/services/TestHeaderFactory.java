package pl.kania.warehousemanager.services;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.provider.Arguments;
import pl.kania.warehousemanager.model.TestClientDetailsFactory;
import pl.kania.warehousemanager.model.TestUserFactory;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.services.security.TokenCreator;

import java.util.stream.Stream;

public class TestHeaderFactory {

    public static String getValidHeaderWithUserRole(WarehouseRole role) {
        return "BEARER " + getValidTokenWithUserRole(role);
    }

    public static String getValidTokenWithUserRole(WarehouseRole role) {
        return TokenCreator.createJWT(TestUserFactory.getUserWithRole(role), TestClientDetailsFactory.getClientDetails(), "test-issuer", "test-audience");
    }

    public static String getValidTokenWithUserRole(WarehouseRole role, ClientDetails clientDetails) {
        return TokenCreator.createJWT(TestUserFactory.getUserWithRole(role), clientDetails, "test-issuer", "test-audience");
    }

    public static Stream<Arguments> getValidHeadersWithToken() {
        String tokenManager = getValidTokenWithUserRole(WarehouseRole.MANAGER);
        String tokenEmployee = getValidTokenWithUserRole(WarehouseRole.EMPLOYEE);

        Stream<Arguments> streamManager = getHeaderAndAdditionalValueStream(tokenManager, tokenManager);
        Stream<Arguments> streamEmployee = getHeaderAndAdditionalValueStream(tokenEmployee, tokenEmployee);
        return Stream.concat(streamManager, streamEmployee);
    }

    public static Stream<Arguments> getValidHeadersWithClientId() {
        ClientDetails client = TestClientDetailsFactory.getClientDetails();
        String tokenManager = getValidTokenWithUserRole(WarehouseRole.MANAGER);
        String tokenEmployee = getValidTokenWithUserRole(WarehouseRole.EMPLOYEE);

        Stream<Arguments> streamManager = getHeaderAndAdditionalValueStream(tokenManager, client.getClientId());
        Stream<Arguments> streamEmployee = getHeaderAndAdditionalValueStream(tokenEmployee, client.getClientId());
        return Stream.concat(streamManager, streamEmployee);
    }

    public static Stream<Arguments> getHeadersWithoutClaimClientId() {
        ClientDetails clientDetails = TestClientDetailsFactory.getClientDetails();
        clientDetails.setClientId(null);

        String tokenManager = getValidTokenWithUserRole(WarehouseRole.MANAGER, clientDetails);
        String tokenEmployee = getValidTokenWithUserRole(WarehouseRole.EMPLOYEE, clientDetails);

        Stream<Arguments> streamManager = getValidHeaderPrefixes().map(h -> Arguments.of(h + tokenManager));
        Stream<Arguments> streamEmployee = getValidHeaderPrefixes().map(h -> Arguments.of(h + tokenEmployee));

        return Stream.concat(streamEmployee, streamManager);
    }

    @NotNull
    private static Stream<Arguments> getHeaderAndAdditionalValueStream(String token, String value) {
        return getValidHeaderPrefixes()
                .map(h -> Arguments.of(h + token, value));
    }

    public static Stream<String> getValidHeaderPrefixes() {
        return Stream.of(
                "BEARER ",
                "Bearer ",
                "bearer "
        );
    }
}
