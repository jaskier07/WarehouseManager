package pl.kania.warehousemanager.services.security.jwt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.services.TestHeaderFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoleCheckerTest {

    private String headerEmployee;
    private String headerManager;

    @BeforeAll
     void initializeHeaders() {
        headerEmployee = TestHeaderFactory.getValidHeaderWithUserRole(WarehouseRole.EMPLOYEE);
        headerManager = TestHeaderFactory.getValidHeaderWithUserRole(WarehouseRole.MANAGER);
    }

    @NullSource
    @ParameterizedTest(name = "Check if user with header containing role Manager has a sufficient role to perform action requiring role {0}")
    @EnumSource(value = WarehouseRole.class)
    void givenUserWithHeaderContainingRoleManagerCheckIfHasRequiredRoleForAnyWarehouseAction(WarehouseRole role) {
        assertTrue(RoleChecker.hasRole(role, headerManager));
    }

    @Test
    void givenUserWithHeaderContainingRoleEmployeeCheckIfHasRequiredRoleForEmployeeAction() {
        assertTrue(RoleChecker.hasRole(WarehouseRole.EMPLOYEE, headerEmployee));
    }

    @Test
    void givenUserWithHeaderContainingRoleEmployeeCheckIfHasNotRequiredRoleForManagerAction() {
        assertFalse(RoleChecker.hasRole(WarehouseRole.MANAGER, headerEmployee));
    }

}