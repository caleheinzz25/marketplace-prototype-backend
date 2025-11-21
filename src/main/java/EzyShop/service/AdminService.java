package EzyShop.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import EzyShop.dto.User.UserDto;
import EzyShop.dto.order.TransactionDto;
import EzyShop.dto.product.ProductDto;
import EzyShop.dto.store.StoreDto;
import EzyShop.exception.BusinessException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.mapper.ProductMapper;
import EzyShop.mapper.StoreMapper;
import EzyShop.mapper.TransactionMapper;
import EzyShop.mapper.UserMapper;
import EzyShop.model.Role;
import EzyShop.model.User;
import EzyShop.model.orders.Transaction;
import EzyShop.model.products.Product;
import EzyShop.model.store.Store;
import EzyShop.repository.ProductRepository;
import EzyShop.repository.RedisTokenRepository;
import EzyShop.repository.StoreRepository;
import EzyShop.repository.TransactionRepository;
import EzyShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final StoreRepository storeRepository;
    private final UserMapper userMapper;
    private final StoreMapper storeMapper;
    private final ProductMapper productMapper;
    private final TransactionMapper transactionMapper;
    private final RedisTokenRepository redisTokenRepository;
    private final PasswordEncoder passwordEncoder;

    // ==============================
    // User Management
    // ==============================
    public List<UserDto> getAllUsers() {
        try {
            List<User> users;

            users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

            return userMapper.toDtoList(users);

        } catch (Exception e) {
            log.error("Failed to fetch users", e);
            throw new BusinessException("Failed to retrieve users", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void disableUser(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }
            if (user.getRole() == Role.SUPER_ADMIN) {
                throw new BusinessException("Cannot delete SUPER_ADMIN user", HttpStatus.FORBIDDEN);
            }
            user.setEnabled(false);
            userRepository.save(user);
            redisTokenRepository.deleteAllTokensByUserId(userId); // 🔁 hapus token terkait
        } catch (Exception e) {
            log.error("Failed to disable user", e);
            throw new BusinessException("Failed to disable user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void enableUser(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }
            user.setEnabled(true);
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to fetch user", e);
            throw new BusinessException("Failed to retrieve user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserDto createUser(UserDto userDto) {
        try {
            // Validasi sederhana (bisa diperluas)
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new BusinessException("Email already exists", HttpStatus.BAD_REQUEST);
            }

            // Mapping DTO ke entity
            User user = userMapper.toEntity(userDto);
            user.setUsername(userDto.getUsername());
            user.setRole(userDto.getRole());
            user.setPassword(passwordEncoder.encode(userDto.getPassword())); // Pastikan password sudah di-hash sebelumnya
            user.setFullName(userDto.getFullName());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setEmail(userDto.getEmail());
            // Atur atribut default
            user.setEnabled(true); // default aktif
            user.setCreatedAt(LocalDateTime.now());

            // Simpan user ke database
            User savedUser = userRepository.save(user);

            // Mapping kembali ke DTO untuk response
            return userMapper.toDto(savedUser);

        } catch (Exception e) {
            log.error("Failed to create user", e);
            throw new BusinessException("Failed to create user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteUser(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }
            if (user.getRole() == Role.SUPER_ADMIN) {
                throw new BusinessException("Cannot delete SUPER_ADMIN user", HttpStatus.FORBIDDEN);
            }
            redisTokenRepository.deleteAllTokensByUserId(userId); // 🔁 hapus token terkait
            userRepository.delete(user);
        } catch (Exception e) {
            log.error("Failed to delete user", e);
            throw new BusinessException("Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==============================
    // Product Management
    // ==============================
    public List<ProductDto> getAllProducts() {
        try {
            List<Product> products;

            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

            return productMapper.toListDtoAll(products);

        } catch (Exception e) {
            log.error("Failed to fetch products", e);
            throw new BusinessException("Failed to retrieve products", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void disableProduct(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            product.setEnabled(false);
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Failed to soft delete product with ID {}", productId, e);
            throw new BusinessException("Failed to delete product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void enableProduct(Long productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            product.setEnabled(true);
            productRepository.save(product);
        } catch (Exception e) {
            log.error("Failed to soft delete product with ID {}", productId, e);
            throw new BusinessException("Failed to delete product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==============================
    // Transaction Management
    // ==============================
    public List<TransactionDto> getLatestTransactions() {
        try {
            List<Transaction> transactions;

            transactions = transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

            return transactionMapper.toDtoList(transactions);

        } catch (Exception e) {
            throw new BusinessException("Failed to retrieve transactions", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==============================
    // Store Management
    // ==============================
    public List<StoreDto> getAllStores() {
        try {
            List<Store> stores;

            stores = storeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

            return storeMapper.toDtoList(stores);

        } catch (Exception e) {
            log.error("Failed to fetch stores", e);
            throw new BusinessException("Failed to retrieve stores", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void disableStore(Long storeId) {
        try {
            Store store = storeRepository.findById(storeId).orElse(null);
            if (store == null) {
                throw new ResourceNotFoundException("Store not found");
            }
            store.setEnabled(false);
            storeRepository.save(store);
        } catch (Exception e) {
            log.error("Failed to fetch store", e);
            throw new BusinessException("Failed to retrieve store", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void enableStore(Long storeId) {
        try {
            Store store = storeRepository.findById(storeId).orElse(null);
            if (store == null) {
                throw new ResourceNotFoundException("Store not found");
            }
            store.setEnabled(true);
            storeRepository.save(store);
        } catch (Exception e) {
            log.error("Failed to fetch store", e);
            throw new BusinessException("Failed to retrieve store", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
