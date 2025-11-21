package EzyShop.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import EzyShop.dto.product.ProductDto;
import EzyShop.model.products.Product;
import EzyShop.service.ProductService;
import EzyShop.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

        private final ProductService productService;
        private final JWTUtils jwtUtils;

        @GetMapping
        // @Cacheable(value = "products", key = "T(java.util.Objects).hash(#limit,
        // #skip, #select, #sortBy, #order, #q, #category, #brand)")
        public ResponseEntity<?> getProducts(
                        @RequestParam(defaultValue = "10") int limit,
                        @RequestParam(defaultValue = "0") int skip,
                        @RequestParam(required = false) String select,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String order,
                        @RequestParam(required = false) String q,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String brand) {
                try {
                        List<String> categories = category == null
                                        ? List.of()
                                        : Arrays.stream(category.split(","))
                                                        .map(String::trim)
                                                        .filter(s -> !s.isBlank())
                                                        .toList();

                        List<String> selectFields = (select == null || select.isBlank())
                                        ? List.of()
                                        : Arrays.stream(select.split(",")).map(String::trim).toList();

                        Map<String, Object> result = productService.findProductsWithMeta(
                                        limit, skip, selectFields, sortBy, order, q, categories, brand);

                        return ResponseEntity.ok(result);
                } catch (IllegalArgumentException e) {
                        log.warn("Bad request: {}", e.getMessage());
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                } catch (Exception e) {
                        log.error("Error while fetching products", e);
                        return ResponseEntity.internalServerError()
                                        .body(Map.of("error", "Internal server error"));
                }
        }

        @PostMapping
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<Product> createProduct(@RequestHeader("Authorization") String authToken,
                        @RequestBody ProductDto productDto) {
                Long userId = jwtUtils.extractUserId(authToken.substring(7));
                Product savedProduct = productService.createProduct(userId, productDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        }

        @GetMapping("/categories")
        public ResponseEntity<List<String>> getAllCategories() {
                List<String> categories = productService.getAllCategories();
                return ResponseEntity.ok(categories);
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getProductById(@PathVariable long id) {
                try {
                        log.info("Fetching product by ID: {}", id);
                        ProductDto product = productService.getProduct(id);
                        return ResponseEntity.ok(product);
                } catch (IllegalArgumentException e) {
                        log.warn("Product with ID {} not found: {}", id, e.getMessage());
                        return ResponseEntity.status(404).body(Map.of("error", "Product not found"));
                } catch (Exception e) {
                        log.error("Error while fetching product ID: {}", id, e);
                        return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error"));
                }
        }

        @DeleteMapping("/store/{productId}")
        public ResponseEntity<?> delProductById(@PathVariable Long productId) {

                productService.softDeleteProduct(productId);

                return ResponseEntity.ok().body(Map.of("message", "product berhasil di hapus"));
        }

        @PostMapping("/store/{productId}")
        public ResponseEntity<?> enableProductById(@PathVariable Long productId) {

                productService.enableProduct(productId);

                return ResponseEntity.ok().body(Map.of("message", "product berhasil active"));
        }

        @GetMapping("/limit/{limit}")
        public ResponseEntity<?> getMethodName(@RequestHeader("Authorization") String authToken,
                        @PathVariable Integer limit) {
                Long userId = jwtUtils.extractUserId(authToken.substring(7));   

                List<ProductDto> products = productService.getOrdersByStoreIdAndLimit(userId, limit);
                return ResponseEntity.ok().body(products);
        }

        @PostMapping("/add")
        public ResponseEntity<?> addProduct(@RequestHeader("Authorization") String authToken,@RequestBody ProductDto entity) {
                Long userId = jwtUtils.extractUserId(authToken.substring(7));
                productService.addProduct(entity, userId);
                return ResponseEntity.ok().body(Map.of("message", "Product update successfully"));
        }

        
        @PutMapping("/update/{productId}")
        public ResponseEntity<?> updateProduct(@RequestHeader("Authorization") String authToken,@RequestBody ProductDto entity,@PathVariable Long productId) {
                Long userId = jwtUtils.extractUserId(authToken.substring(7));
                productService.updateProduct( productId,entity, userId  );
                return ResponseEntity.ok().body(Map.of("message", "Product added successfully"));
        }

}
