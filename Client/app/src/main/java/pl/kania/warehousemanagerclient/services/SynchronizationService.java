package pl.kania.warehousemanagerclient.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pl.kania.warehousemanagerclient.model.IdType;
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnDevice;
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnServer;
import pl.kania.warehousemanagerclient.model.entities.Product;
import pl.kania.warehousemanagerclient.services.dao.DatabaseManager;

public class SynchronizationService {

    private static final String ERROR_PREFIX = "!ERROR! ";
    private final DatabaseManager db;
    private final ConfigurationProvider cp;

    public SynchronizationService(DatabaseManager db, ConfigurationProvider cp) {
        this.db = db;
        this.cp = cp;
    }

    public DataToSyncOnServer getDataToSynchronizeOnServer() {
        DataToSyncOnServer dataToSyncOnServer = new DataToSyncOnServer();

        final List<Product> allNonRemovedProducts = db.selectAllNonRemovedProducts();
        dataToSyncOnServer.setProducts(allNonRemovedProducts);

        final List<Long> removedProductIds = db.selectRemovedProductGlobalIds();
        dataToSyncOnServer.setRemovedProductsIds(removedProductIds);

        return dataToSyncOnServer;
    }

    public List<String> updateDataOnDevice(DataToSyncOnDevice dataToSyncOnDevice) {
        List<String> messages = new ArrayList<>();
        addNewProducts(dataToSyncOnDevice.getNewProducts(), messages);
        removeProducts(dataToSyncOnDevice.getRemovedIds(), messages);
        // TODO updated products

        return messages;
    }

    private void removeProducts(List<Long> removedIds, List<String> messages) {
        for (Long id : removedIds) {
            final Optional<Product> product = db.selectProduct(id, IdType.GLOBAL);
            if (product.isPresent()) {
                final boolean result = db.deleteProduct(id, IdType.GLOBAL);
                if (result) {
                    messages.add("Product (id " + id + ") has been removed.");
                } else {
                    messages.add(ERROR_PREFIX + "Cannot remove product (id " + id + ").");
                }
            } else {
                Log.w("removing product", "Product not found (globalId = " + id);
                messages.add(ERROR_PREFIX + "Cannot remove product (id " + id + "). Product not found.");
            }
        }
    }

    private void addNewProducts(List<Product> newProducts, List<String> messages) {
        if (!newProducts.isEmpty()) {
            try {
                boolean result = db.insertAllProducts(newProducts);
                if (result) {
                    messages.add("All products (" + newProducts.size() + ") have been added");
                } else {
                    messages.add(ERROR_PREFIX + "Not all products have been added.");
                }
            } catch (Exception e) {
                Log.e("vector clock", "An error occured while changing vector clock", e);
            }
        }
    }
}
