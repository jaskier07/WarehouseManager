package pl.kania.warehousemanager.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import pl.kania.warehousemanager.dao.ProductRepository;
import pl.kania.warehousemanager.model.Product;

import javax.ws.rs.QueryParam;
import java.net.URI;
import java.util.Optional;

@RestController
public class ProductResource {

    @Autowired
    private ProductRepository productDao;

    @GetMapping("/products")
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        return ResponseEntity.ok(productDao.findAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") Long productId) {
        Optional<Product> product = productDao.findById(productId);
        if (!product.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product.get());
    }

    @PostMapping("/product")
    public ResponseEntity<Void> addProduct(@RequestBody Product product) {
        if (product == null) {
            return getResponseEntityNullParameter();
        }

        product.setQuantity(0);
        product = productDao.save(product);
        URI uri = UriComponentsBuilder.fromUriString("/product/" + product.getId()).build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/product/{productId}/update")
    public ResponseEntity<Void> updateProduct(@RequestBody Product product, @PathVariable("productId") Long productId) {
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
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") Long productId) {
        if (productId == null) {
            return getResponseEntityNullParameter();
        }

        productDao.deleteById(productId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/product/{productId}/increase")
    public ResponseEntity<Void> increaseProductQuantityBy(@RequestBody Integer quantity, @PathVariable("productId") Long productId) {
        if (quantity == null || productId == null) {
            return getResponseEntityNullParameter();
        }

        if (productNotExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        if (!productDao.increaseProductQuantityBy(quantity, productId)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/product/{productId}/decrease")
    public ResponseEntity<Void> decreaseProductQuantityBy(@RequestBody Integer quantity, @PathVariable("productId") Long productId) {
        if (quantity == null || productId == null) {
            return getResponseEntityNullParameter();
        }

        if (productNotExists(productId)) {
            return ResponseEntity.notFound().build();
        }
        if (!productDao.decreaseProductQuantityBy(quantity, productId)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().build();
    }

    private boolean productNotExists(@QueryParam("productId") Long productId) {
        return !productDao.existsById(productId);
    }

    private ResponseEntity<Void> getResponseEntityNullParameter() {
        return ResponseEntity.badRequest().build();
    }

}
