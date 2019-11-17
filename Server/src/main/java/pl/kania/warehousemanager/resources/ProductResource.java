package pl.kania.warehousemanager.resources;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import pl.kania.warehousemanager.dao.ProductRepository;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.Product;
import pl.kania.warehousemanager.model.dto.ChangeQuantityResult;
import pl.kania.warehousemanager.security.JWTService;

import javax.ws.rs.QueryParam;
import java.net.URI;
import java.util.Optional;

@RestController
public class ProductResource {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProductRepository productDao;

    @GetMapping("/products")
    public ResponseEntity<Iterable<Product>> getAllProducts(@RequestHeader("Authorization") String header) {
        if (userDoesNotHavePermission(WarehouseRole.EMPLOYEE, header)) {
            return getResponseUnauthorized();
        }
        return ResponseEntity.ok(productDao.findAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") Long productId, @RequestHeader("Authorization") String header) {
        if (userDoesNotHavePermission(WarehouseRole.EMPLOYEE, header)) {
            return getResponseUnauthorized();
        }
        Optional<Product> product = productDao.findById(productId);
        if (!product.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product.get());
    }

    @PostMapping("/product")
    public ResponseEntity<Void> addProduct(@RequestBody Product product, @RequestHeader("Authorization") String header) {
        if (userDoesNotHavePermission(WarehouseRole.EMPLOYEE, header)) {
            return getResponseUnauthorized();
        }
        if (product == null) {
            return getError();
        }

        product.setQuantity(0);
        product = productDao.save(product);
        URI uri = UriComponentsBuilder.fromUriString("/product/" + product.getId()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/product/{productId}/update")
    public ResponseEntity<Void> updateProduct(@RequestBody Product product, @PathVariable("productId") Long productId, @RequestHeader("Authorization") String header) {
        if (userDoesNotHavePermission(WarehouseRole.EMPLOYEE, header)) {
            return getResponseUnauthorized();
        }
        Optional<Product> productOpt = productDao.findById(productId);
        if (!productOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Product productFound = productOpt.get();
        productFound.update(product);
        productDao.save(productFound);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/product/{productId}/deletion")
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId, @RequestHeader("Authorization") String header) {
        if (userDoesNotHavePermission(WarehouseRole.MANAGER, header)) {
            return getResponseUnauthorized();
        }
        if (productId == null) {
            return getError();
        }

        productDao.deleteById(productId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/product/{productId}/increase")
    public ResponseEntity<Void> increaseProductQuantityBy(@RequestBody Integer quantity, @PathVariable("productId") Long productId, @RequestHeader("Authorization") String header) {
        if (userDoesNotHavePermission(WarehouseRole.EMPLOYEE, header)) {
            return getResponseUnauthorized();
        }
        if (quantity == null || productId == null) {
            return getError();
        }

        if (productNotExists(productId)) {
            return getError();
        }
        if (!productDao.increaseProductQuantityBy(quantity, productId)) {
            return getError();
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/product/{productId}/decrease")
    public ResponseEntity<ChangeQuantityResult> decreaseProductQuantityBy(@RequestBody Integer quantity, @PathVariable("productId") Long productId, @RequestHeader("Authorization") String header) {
        if (userDoesNotHavePermission(WarehouseRole.EMPLOYEE, header)) {
            return getResponseUnauthorized();
        }
        if (quantity == null || productId == null) {
            return getChangeQuantityError("No quantity or productId");
        }

        if (productNotExists(productId)) {
            return getChangeQuantityError("Product does not exists");
        }
        if (!productDao.decreaseProductQuantityBy(quantity, productId)) {
            return getChangeQuantityError("Cannot decrease product quantity by this value");
        }
        return ResponseEntity.ok(new ChangeQuantityResult(true));
    }

    @NotNull
    private <T> ResponseEntity<T> getResponseUnauthorized() {
        return ResponseEntity.status(401).build();
    }

    private boolean userDoesNotHavePermission(WarehouseRole role, @RequestHeader("Authorization") String header) {
        return !jwtService.hasRole(role, header);
    }

    private boolean productNotExists(@QueryParam("productId") Long productId) {
        return !productDao.existsById(productId);
    }

    private ResponseEntity<ChangeQuantityResult> getChangeQuantityError(String error) {
        return ResponseEntity.badRequest().body(new ChangeQuantityResult(false, error));
    }

    private ResponseEntity<Void> getError() {
        return ResponseEntity.badRequest().build();
    }

}
