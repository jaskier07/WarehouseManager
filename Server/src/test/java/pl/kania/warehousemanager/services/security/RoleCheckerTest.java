package pl.kania.warehousemanager.services.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import pl.kania.warehousemanager.factory.TestHeaderFactory;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.stereotype.RequiredSpringContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredSpringContext
class RoleCheckerTest {

    @Autowired
    private TestHeaderFactory testHeaderFactory;

    @Autowired
    private RoleChecker roleChecker;

    @ParameterizedTest(name = "Check if user with header containing role Manager has a sufficient role to perform action requiring role {0}")
    @EnumSource(value = WarehouseRole.class)
    void givenUserWithHeaderContainingRoleManagerCheckIfHasRequiredRoleForAnyWarehouseAction(WarehouseRole role) {
        String headerManager = testHeaderFactory.getValidHeaderWithUserRole(WarehouseRole.MANAGER);
        assertTrue(roleChecker.hasRole(role, headerManager));
    }

    @Test
    void givenUserWithHeaderContainingRoleEmployeeCheckIfHasRequiredRoleForEmployeeAction() {
        String headerEmployee = testHeaderFactory.getValidHeaderWithUserRole(WarehouseRole.EMPLOYEE);
        assertTrue(roleChecker.hasRole(WarehouseRole.EMPLOYEE, headerEmployee));
    }

    @Test
    void givenUserWithHeaderContainingRoleEmployeeCheckIfHasNotRequiredRoleForManagerAction() {
        String headerEmployee = testHeaderFactory.getValidHeaderWithUserRole(WarehouseRole.EMPLOYEE);
        assertFalse(roleChecker.hasRole(WarehouseRole.MANAGER, headerEmployee));
    }

}