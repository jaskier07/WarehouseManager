package pl.kania.warehousemanager.model.dto;

import lombok.Value;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.db.User;

import java.util.List;

@Value
public class DataToSyncOnDevice {
    // nowi użytkownicy
    // zmodyfikowani użytkownicy (role)

    private List<User> newUsers;
    private List<Product> products;
}
