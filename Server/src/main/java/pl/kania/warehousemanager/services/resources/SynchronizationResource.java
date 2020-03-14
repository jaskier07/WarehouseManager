package pl.kania.warehousemanager.services.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.dto.DataToSyncOnClient;
import pl.kania.warehousemanager.model.dto.DataToSyncOnServer;
import pl.kania.warehousemanager.services.security.JWTService;
import pl.kania.warehousemanager.services.security.RoleChecker;
import pl.kania.warehousemanager.services.synchronization.SynchronizationService;

import java.util.Optional;

@RestController
public class SynchronizationResource {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SynchronizationService synchronizationService;

    @PostMapping(path = "/synchronize")
    private ResponseEntity<DataToSyncOnClient> synchronizeWithDevice(@RequestBody DataToSyncOnServer dataToSyncOnServer, @RequestHeader("Authorization") String authorization) {
        if (!RoleChecker.hasRole(WarehouseRole.EMPLOYEE, authorization)) {
            return ResponseEntity.status(401).build();
        }

        final Optional<String> clientId = jwtService.getClientId(authorization);
        if (!clientId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        final DataToSyncOnClient dataToSyncOnDevice = synchronizationService.synchronizeWithDevice(dataToSyncOnServer, clientId.get());
        return ResponseEntity.ok(dataToSyncOnDevice);
    }
}
