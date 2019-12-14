package pl.kania.warehousemanagerclient.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pl.kania.warehousemanagerclient.model.IdType;
import pl.kania.warehousemanagerclient.model.dto.DataToSyncOnClient;
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
        final DataToSyncOnServer dataToSyncOnServer = new DataToSyncOnServer();

        final List<Product> newProducts = db.selectAllNonRemovedNewProducts();
        dataToSyncOnServer.setNewProducts(newProducts);

        final List<Product> existingProducts = db.selectAllNonRemovedProductsWithGlobalId();
        dataToSyncOnServer.setExistingProducts(existingProducts);

        final List<Long> removedProductIds = db.selectRemovedProductGlobalIds();
        dataToSyncOnServer.setRemovedProductsIds(removedProductIds);

        return dataToSyncOnServer;
    }

    public List<String> updateDataOnDevice(DataToSyncOnClient dataToSyncOnClient) {
        final List<String> messages = new ArrayList<>();
        addNewProducts(dataToSyncOnClient.getNewProducts(), messages);
        removeProducts(dataToSyncOnClient.getRemovedIds(), messages);
        updateSavedProducts(dataToSyncOnClient.getSavedProductIdPerLocalId(), messages);
        updateProducts(dataToSyncOnClient.getUpdatedProducts(), messages);
        // TODO update quantity

        return messages;
    }

    private void updateProducts(List<Product> updatedProducts, List<String> messages) {
        for (Product product : updatedProducts) {
            Product productFromDb = db.selectProduct(product.getId(), IdType.GLOBAL).get();

            boolean result = db.updateNonQuantityProductValues(product, IdType.GLOBAL, false);
            if (result) {
                messages.add("Product has been merged with server (id " + product.getId() + "): " + product.getChangedInfo(productFromDb));
            } else {
                messages.add(ERROR_PREFIX + "An error occured while merging product with server (id " + product.getId() + ")");
            }
        }
    }

    private void updateSavedProducts(Map<Long, Long> savedProductIdPerLocalId, List<String> messages) {
        for (Map.Entry<Long, Long> productIdPerLocalId : savedProductIdPerLocalId.entrySet()) {
            final Optional<Product> product = db.selectProduct(productIdPerLocalId.getKey(), IdType.LOCAL);
            if (product.isPresent()) {
                product.get().setId(productIdPerLocalId.getValue());
                db.updateNonQuantityProductValues(product.get(), IdType.LOCAL, false);
                messages.add("Product global id updated: " + product.toString());
            } else {
                messages.add("Cannot set product global id (" + productIdPerLocalId.getValue() + ") because product with id (" + productIdPerLocalId.getKey() + " does not exist");
            }
        }
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
                boolean result = db.insertAllProducts(newProducts, true, false);
                if (result) {
                    messages.add("All products (" + newProducts.toString() + ") have been added");
                } else {
                    messages.add(ERROR_PREFIX + "Not all products have been added.");
                }
            } catch (Exception e) {
                Log.e("vector clock", "An error occured while changing vector clock", e);
            }
        }
    }
}
