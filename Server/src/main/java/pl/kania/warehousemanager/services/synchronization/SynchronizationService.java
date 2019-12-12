package pl.kania.warehousemanager.services.synchronization;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.dto.DataToSyncOnDevice;
import pl.kania.warehousemanager.model.dto.DataToSyncOnServer;

@Service
public class SynchronizationService {

    public DataToSyncOnDevice synchronizeWithDevice(@NonNull DataToSyncOnServer dataToSyncOnServer, String s) {
        return null;
    }
}
