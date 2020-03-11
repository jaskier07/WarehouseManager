package pl.kania.warehousemanager.model;

import java.sql.Timestamp;

public interface TransferableProduct {

    Long getId();

    String getManufacturerName();

    String getModelName();

    Double getPrice();

    boolean isRemoved();

    Timestamp getLastModified();

}
