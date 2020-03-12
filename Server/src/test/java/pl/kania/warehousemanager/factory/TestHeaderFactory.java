package pl.kania.warehousemanager.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.services.security.JWTService;

@Service
public class TestHeaderFactory {

    @Autowired
    private JWTService jwtService;

    public String getValidHeaderWithUserRole(WarehouseRole role) {
        return "BEARER " + getValidTokenWithUserRole(role);
    }

    public String getValidTokenWithUserRole(WarehouseRole role) {
        return jwtService.createJwt(TestUserFactory.getUserWithRole(role), TestClientDetailsFactory.getClientDetails());
    }
}
