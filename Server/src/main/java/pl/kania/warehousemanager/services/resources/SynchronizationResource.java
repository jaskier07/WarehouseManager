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
import pl.kania.warehousemanager.services.security.jwt.JWTService;
import pl.kania.warehousemanager.services.synchronization.SynchronizationService;

import java.util.Optional;

@RestController
public class SynchronizationResource {

    private SynchronizationService synchronizationService;
    private JWTService jwtService;

    public SynchronizationResource(@Autowired SynchronizationService syncService, @Autowired JWTService jwtService) {
        this.synchronizationService = syncService;
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/synchronize")
    private ResponseEntity<DataToSyncOnClient> synchronizeWithDevice(@RequestBody DataToSyncOnServer dataToSyncOnServer, @RequestHeader("Authorization") String authorization) {
        if (!jwtService.hasUserInTokenHeaderRequiredRole(WarehouseRole.EMPLOYEE, authorization)) {
            return ResponseEntity.status(401).build();
        }

        final Optional<String> clientId = jwtService.extractClientIdFromHeader(authorization);
        if (!clientId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        final DataToSyncOnClient dataToSyncOnDevice = synchronizationService.synchronizeWithDevice(dataToSyncOnServer, clientId.get());
        return ResponseEntity.ok(dataToSyncOnDevice);
    }
}
