package pl.kania.warehousemanager.model.dto;

import lombok.Getter;
import lombok.Setter;
import pl.kania.warehousemanager.model.db.Product;

import java.util.List;

@Getter
@Setter
public class DataToSyncOnServer {
    // czy użytkownikowi zmieniła się rola?
    // POTRZEBNE: lista(id, rola, wektor modyfikacji)

    // czy dodano nowe produkty?
    // POTRZEBNE: lista(produkt, wektor) których wektor na wszystkich innych urządzeniach to 0
    // czy usunięto produkt?
    // POTRZEBNE: lista wszystkich id produktów (któregoś brakuje na serwerze to usuwam z serwera)
    // czy zmodyfikowano wartości opisowe?
    // POTRZEBNE: lista(produkt, wektor) i merge na podstawie wektora
    // czy zmodyfikowano ilość produktu?
    // POTRZEBNE: lista(id, ilość, wektor) i merge na podstawie wektora

    // CZYLI: trzeba przesłać wszystkie produkty
    private List<Product> newProducts;
    private List<Product> existingProducts;
    private List<Long> removedProductsIds;
}
