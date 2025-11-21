package EzyShop.controller;

import EzyShop.dto.User.UserDto;
import EzyShop.dto.order.TransactionDto;
import EzyShop.dto.product.ProductDto;
import EzyShop.dto.store.StoreDto;
import EzyShop.model.User;
import EzyShop.model.orders.Transaction;
import EzyShop.model.products.Product;
import EzyShop.model.store.Store;
import EzyShop.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // === USERS ===

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<UserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        adminService.createUser(userDto);
        return ResponseEntity.ok(Map.of("message", "User created successfully"));
    }

    @DeleteMapping("/user/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable long id) {
        adminService.disableUser(id);
        return ResponseEntity.ok(Map.of("message", "User disabled successfully"));
    }

    @PutMapping("/user/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable long id) {
        adminService.enableUser(id);
        return ResponseEntity.ok(Map.of("message", "User enabled successfully"));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // === PRODUCTS ===

    @GetMapping("/products")
    public ResponseEntity<?> getProducts() {
        List<ProductDto> products = adminService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/products/{id}/disable")
    public ResponseEntity<?> disableProduct(@PathVariable long id) {
        adminService.disableProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product disabled successfully"));
    }

    @PutMapping("/products/{id}/enable")
    public ResponseEntity<?> enableProduct(@PathVariable long id) {
        adminService.enableProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product enabled successfully"));
    }

    // === STORES ===

    @GetMapping("/stores")
    public ResponseEntity<?> getStores() {
        List<StoreDto> stores = adminService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    @DeleteMapping("/stores/{id}/disable")
    public ResponseEntity<?> disableStore(@PathVariable long id) {
        adminService.disableStore(id);
        return ResponseEntity.ok(Map.of("message", "Store disabled successfully"));
    }

    @PutMapping("/stores/{id}/enable")
    public ResponseEntity<?> enableStore(@PathVariable long id) {
        adminService.enableStore(id);
        return ResponseEntity.ok(Map.of("message", "Store enabled successfully"));
    }

    // === TRANSACTIONS ===

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions() {
        List<TransactionDto> transactions = adminService.getLatestTransactions();
        return ResponseEntity.ok(transactions);
    }
}
