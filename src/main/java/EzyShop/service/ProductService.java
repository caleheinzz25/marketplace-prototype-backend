package EzyShop.service;

import java.math.BigDecimal;
import java.security.DrbgParameters.Reseed;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import EzyShop.dto.product.DimensionDto;
import EzyShop.dto.product.ListProductRequest;
import EzyShop.dto.product.MetaDto;
import EzyShop.dto.product.ProductDto;
import EzyShop.dto.product.ProductResponse;
import EzyShop.dto.product.ReviewDto;
import EzyShop.exception.BusinessException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.mapper.ProductImageMapper;
import EzyShop.mapper.ProductMapper;
import EzyShop.model.Role;
import EzyShop.model.User;
import EzyShop.model.products.Dimension;
import EzyShop.model.products.Product;
import EzyShop.model.products.ProductImage;
import EzyShop.model.products.ProductMeta;
import EzyShop.model.products.Review;
import EzyShop.model.store.Store;
import EzyShop.repository.ProductRepository;
import EzyShop.repository.StoreRepository;
import EzyShop.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductMapper productMapper;

    public static char getCharFromIndex(int index) {
        if (index < 0 || index > 25) {
            throw new IllegalArgumentException("Index harus antara 0 dan 25");
        }
        return (char) ('a' + index);
    }

    public void saveBulkProducts(ListProductRequest requestDto) {
        List<ProductDto> products = requestDto.getProducts();

        List<User> existingUsers = userRepository.findAll();
        List<Store> existingStores = storeRepository.findAll();

        AtomicInteger now = new AtomicInteger(0);

        for (int i = 0; i < products.size(); i++) {
            final int idx = i;
            ProductDto dto = products.get(idx);

            // Cari store yang storeType-nya sama dengan kategori produk
            Store matchedStore = existingStores.stream()
                    .filter(s -> s.getStoreType().equals(dto.getCategory()))
                    .findFirst()
                    .orElse(null);

            if (matchedStore == null) {
                int current = now.getAndIncrement();

                // Jika tidak ada store yang cocok, buat User dan Store baru
                String storeName = "store_" + getCharFromIndex(current);
                String username = "cale_" + getCharFromIndex(current);
                String email = "cale_" + getCharFromIndex(current) + "@gmail.com";
                String storeNo = "STORE_" + idx;

                // Cari user berdasarkan username
                User user = existingUsers.stream()
                        .filter(u -> u.getUsername().equals(username))
                        .findFirst()
                        .orElseGet(() -> {
                            User newUser = new User();
                            newUser.setUsername(username);
                            newUser.setPassword(passwordEncoder.encode("fireflies" + idx));
                            newUser.setEmail(email);
                            newUser.setFullName("caleheinzz " + getCharFromIndex(current));
                            newUser.setRole(Role.SELLER);
                            return userRepository.save(newUser);
                        });
                System.out.println("fireflies" + idx);         
                // Simpan store baru
                matchedStore = storeRepository.save(
                        Store.builder()
                                .storeName(storeName)
                                .storeNo(storeNo)
                                .logUrl("logo-" + idx + ".png")
                                .description("Deskripsi toko ke-" + idx)
                                .storeType(dto.getCategory())
                                .saldo(BigDecimal.ZERO)
                                .owner(user)
                                .build());

                existingUsers.add(user);
                existingStores.add(matchedStore); // Tambahkan ke cache agar tidak dibuat ulang
            }

            // Pastikan hanya memproses produk jika ada store yang cocok
            if (matchedStore != null && matchedStore.getStoreType().equals(dto.getCategory())) {
                Product product = new Product();
                product.setTitle(dto.getTitle());
                product.setDescription(dto.getDescription());
                product.setCategory(dto.getCategory());
                product.setPrice(dto.getPrice().multiply(BigDecimal.valueOf(3000)));
                product.setRating(dto.getRating());
                product.setStock(dto.getStock());
                product.setTags(new HashSet<>(dto.getTags()));
                product.setBrand(dto.getBrand());
                product.setSku(dto.getSku());
                product.setWeight(dto.getWeight());
                product.setWarrantyInformation(dto.getWarrantyInformation());
                product.setShippingInformation(dto.getShippingInformation());
                product.setAvailabilityStatus(dto.getAvailabilityStatus());
                product.setReturnPolicy(dto.getReturnPolicy());
                product.setMinimumOrderQuantity(dto.getMinimumOrderQuantity());
                product.setThumbnail(dto.getThumbnail());
                product.setEnabled(true);
                product.setStore(matchedStore);

                // Meta
                ProductMeta meta = new ProductMeta();
                meta.setCreatedAt(dto.getMeta().getCreatedAt());
                meta.setUpdatedAt(dto.getMeta().getUpdatedAt());
                meta.setBarcode(dto.getMeta().getBarcode());
                meta.setQrCode(dto.getMeta().getQrCode());
                product.setMeta(meta);

                // Dimensions
                Dimension dim = new Dimension();
                dim.setWidth(dto.getDimensions().getWidth());
                dim.setHeight(dto.getDimensions().getHeight());
                dim.setDepth(dto.getDimensions().getDepth());
                product.setDimensions(dim);

                // Reviews
                List<Review> reviews = dto.getReviews().stream().map(r -> {
                    Review review = new Review();
                    review.setRating(r.getRating());
                    review.setComment(r.getComment());
                    review.setDate(r.getDate());
                    review.setReviewerName(r.getReviewerName());
                    review.setReviewerEmail(r.getReviewerEmail());
                    review.setProduct(product);
                    return review;
                }).toList();
                product.setReviews(reviews);

                // Images
                List<ProductImage> images = dto.getImages().stream().map(url -> {
                    ProductImage image = new ProductImage();
                    image.setImageUrl(url);
                    image.setProduct(product);
                    return image;
                }).toList();
                product.setImages(images);

                productRepository.save(product);
            }
        }
    }

    public Product createProduct(Long userId, ProductDto dto) {
        Optional<Product> existing = productRepository.findByTitle(dto.getTitle());
        if (existing.isPresent()) {
            throw new RuntimeException("Product with title " + dto.getTitle() + " already exists");
        }

        Product product = new Product();

        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice()); // konversi jika perlu
        product.setRating(dto.getRating());
        product.setStock(dto.getStock());
        product.setTags(new HashSet<>(dto.getTags()));
        product.setBrand(dto.getBrand());
        product.setSku(dto.getSku());
        product.setWeight(dto.getWeight());
        product.setWarrantyInformation(dto.getWarrantyInformation());
        product.setShippingInformation(dto.getShippingInformation());
        product.setAvailabilityStatus(dto.getAvailabilityStatus());
        product.setReturnPolicy(dto.getReturnPolicy());
        product.setMinimumOrderQuantity(dto.getMinimumOrderQuantity());
        product.setThumbnail(dto.getThumbnail());

        // Dapatkan user dan store
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        Store store = new Store();
        store.setId(user.getStore().getId());
        product.setStore(store);

        // Meta
        ProductMeta meta = new ProductMeta();
        meta.setCreatedAt(Instant.now());
        meta.setUpdatedAt(Instant.now());

        // Barcode acak 13 digit
        String randomBarcode = String.valueOf(1_000_000_000_000L + new Random().nextLong(9_000_000_000_000L));
        meta.setBarcode(randomBarcode);

        // QR code simulasi
        String qrCodeUrl = "https://caleheinzz.my.id/products/" + dto.getTitle().replaceAll(" ", "-").toLowerCase();
        meta.setQrCode(qrCodeUrl);

        product.setMeta(meta);

        // Dimensions
        Dimension dim = new Dimension();
        dim.setWidth(dto.getDimensions().getWidth());
        dim.setHeight(dto.getDimensions().getHeight());
        dim.setDepth(dto.getDimensions().getDepth());
        product.setDimensions(dim);

        // Images
        List<ProductImage> images = dto.getImages().stream().map(url -> {
            ProductImage image = new ProductImage();
            image.setImageUrl(url);
            image.setProduct(product);
            return image;
        }).toList();
        product.setImages(images);

        return productRepository.save(product);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<ProductDto> getOrdersByStoreIdAndLimit(Long userId, Integer limit) {
        int maxResults = (limit != null && limit > 0) ? limit : 5;

        Store store = storeRepository.findByOwner(User.builder().id(userId).build())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with owner id: " + userId));

        List<Product> products = entityManager.createNativeQuery("""
                    SELECT * FROM products
                    WHERE store_id = :storeId
                    ORDER BY created_at DESC
                    LIMIT :maxResults
                """, Product.class)
                .setParameter("storeId", store.getId())
                .setParameter("maxResults", maxResults)
                .getResultList();

        return productMapper.toListDtoAll(products);
    }

    public List<String> getAllCategories() {
        return productRepository.findAll()
                .stream()
                .map(Product::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public ProductDto getProduct(Long id) {
        if (!productRepository.existsByIdAndEnabledTrue(id)) {
            throw new ResourceNotFoundException("Product tidak tersedia atau telah di-nonaktifkan");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found "));

        return productMapper.toDto(product);
    }

    public Map<String, Object> findProductsWithMeta(
            int limit,
            int skip,
            List<String> selectFields,
            String sortBy,
            String order,
            String q,
            List<String> categories, // <-- sebelumnya: String category
            String brand) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // === Query Utama ===
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<Product> root = cq.from(Product.class);

        boolean selectAll = selectFields == null || selectFields.isEmpty();
        List<Selection<?>> selections = new ArrayList<>();

        if (selectAll) {
            selections.add(root);
        } else {
            for (String field : selectFields) {
                selections.add(root.get(field).alias(field));
            }
        }

        cq.multiselect(selections);

        List<Predicate> predicates = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + q.toLowerCase() + "%")));
        }

        predicates.add(cb.isTrue(root.get("enabled")));
        if (categories != null && !categories.isEmpty()) {
            List<String> lowerCategories = categories.stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(String::toLowerCase)
                    .toList();

            if (!lowerCategories.isEmpty()) {
                predicates.add(cb.lower(root.get("category")).in(lowerCategories));
            }
        }

        if (brand != null && !brand.isBlank()) {
            predicates.add(cb.equal(root.get("brand"), brand));
        }

        Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
        cq.where(predicateArray);

        // === Sorting ===
        List<Order> orderList = new ArrayList<>();
        if (!selectAll && !selectFields.contains(sortBy)) {
            throw new IllegalArgumentException("sortBy must be in selected fields");
        }

        orderList.add(order.equalsIgnoreCase("desc") ? cb.desc(root.get(sortBy)) : cb.asc(root.get(sortBy)));

        // Fallback to ID for stable pagination
        if (!"id".equals(sortBy)) {
            orderList.add(cb.asc(root.get("id")));
        }
        cq.orderBy(orderList);

        // === Eksekusi query ===
        TypedQuery<Tuple> query = entityManager.createQuery(cq);
        query.setFirstResult(skip);
        query.setMaxResults(limit);
        List<Tuple> result = query.getResultList();

        List<Object> products;
        if (selectAll) {
            products = result.stream()
                    .map(t -> (Object) toProductResponseDto(t.get(0, Product.class)))
                    .toList();
        } else {
            products = result.stream()
                    .map(t -> {
                        Map<String, Object> map = new HashMap<>();
                        for (String field : selectFields) {
                            map.put(field, t.get(field));
                        }
                        return (Object) map;
                    }).toList();
        }

        // === Total Count Query ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicateArray);

        // === Response ===
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("products", products);
        response.put("total", products.size());
        response.put("skip", skip);
        response.put("limit", limit);

        return response;
    }

    public Map<String, Object> findProductsWithMetaForSeller(
            int limit,
            int skip,
            String sortBy,
            String order,
            String q,
            String availabilityStatus,
            Long userId) {

        Store store = storeRepository.findByOwner(User.builder().id(userId).build()).orElseThrow(
                () -> new ResourceNotFoundException("Store not found "));
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // Pencarian full-text sederhana pada title dan description
        if (q != null && !q.isBlank()) {
            String keyword = "%" + q.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)));
        }

        // Filter berdasarkan status ketersediaan
        if (availabilityStatus != null && !availabilityStatus.isBlank()) {
            predicates.add(cb.equal(root.get("availability_status"), availabilityStatus));
        }

        // Filter berdasarkan storeId
        if (store.getId() != null) {
            predicates.add(cb.equal(root.get("store").get("id"), store.getId()));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // Sorting
        List<Order> orderList = new ArrayList<>();
        orderList.add("desc".equalsIgnoreCase(order)
                ? cb.desc(root.get(sortBy))
                : cb.asc(root.get(sortBy)));

        if (!"id".equals(sortBy)) {
            orderList.add(cb.asc(root.get("id")));
        }
        cq.orderBy(orderList);

        // Query data utama
        TypedQuery<Product> query = entityManager.createQuery(cq);
        query.setFirstResult(skip);
        query.setMaxResults(limit);

        List<Product> result = query.getResultList();

        // Mapping ke DTO atau response
        List<ProductResponse> products = result.stream()
                .map(this::toProductResponseDto)
                .toList();

        // Query total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // Response Map
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("products", products);
        response.put("total", totalCount);
        response.put("skip", skip);
        response.put("limit", limit);

        return response;
    }

    @Transactional
    public void addProduct(ProductDto productDto, Long userId) {
        // Step 1: Validate the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. Please log in first."));

        // Step 2: Validate the user's store
        Store store = user.getStore();
        if (store == null || store.getId() == null) {
            throw new ResourceNotFoundException("Store not found. Please register your store first.");
        }

        // Step 3: Ensure the store exists in the database
        Store validatedStore = storeRepository.findById(store.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found. Please register your store first."));

        // Step 4: Set the store ID in the DTO
        productDto.setStoreId(validatedStore.getId());
        log.info("{}", productDto.getId());

        // Step 5: Convert DTO to Entity
        Product product = productMapper.toEntity(productDto);

        // Step 6: Save the product
        try {
            productRepository.save(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save product. Please try again.", e);
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductDto productDto, Long userId) {
        // Step 1: Validate the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found. Please log in first."));

        // Step 2: Validate the user's store
        Store store = user.getStore();
        if (store == null || store.getId() == null) {
            throw new ResourceNotFoundException("Store not found. Please register your store first.");
        }

        // Step 3: Ensure the store exists in the database
        Store validatedStore = storeRepository.findById(store.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found. Please register your store first."));

        // Step 4: Fetch the existing product by ID
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found. Please check the product ID."));

        // Step 5: Ensure the product belongs to the user's store
        if (!existingProduct.getStore().getId().equals(validatedStore.getId())) {
            throw new IllegalArgumentException("You are not authorized to update this product.");
        }

        // Step 6: Update the product fields from the DTO
        productMapper.updateEntityFromDto(productDto, existingProduct);

        // Step 7: Save the updated product
        try {
            productRepository.save(existingProduct);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product. Please try again.", e);
        }
    }

    @Transactional
    public void softDeleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.getEnabled()) {
            throw new BusinessException("Product already deleted :" + product.getTitle(), HttpStatus.BAD_REQUEST);
        }

        product.setEnabled(false);
        productRepository.save(product);
    }

    @Transactional
    public void enableProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (Boolean.TRUE.equals(product.getEnabled())) {
            throw new BusinessException("Product is already enabled: " + product.getTitle(), HttpStatus.BAD_REQUEST);
        }

        product.setEnabled(true);
        productRepository.save(product);
    }

    private ProductResponse toProductResponseDto(Product product) {
        // Konversi Dimensions
        DimensionDto dimensionDto = null;
        if (product.getDimensions() != null) {
            dimensionDto = DimensionDto.builder()
                    .width(product.getDimensions().getWidth())
                    .height(product.getDimensions().getHeight())
                    .depth(product.getDimensions().getDepth())
                    .build();
        }

        // Konversi Reviews
        List<ReviewDto> reviewDtos = product.getReviews() != null
                ? product.getReviews().stream()
                        .map(r -> ReviewDto.builder()
                                .rating(r.getRating())
                                .comment(r.getComment())
                                .date(r.getDate())
                                .reviewerName(r.getReviewerName())
                                .reviewerEmail(r.getReviewerEmail())
                                .build())
                        .toList()
                : List.of();

        // Konversi Meta
        MetaDto metaDto = null;
        if (product.getMeta() != null) {
            metaDto = MetaDto.builder()
                    .createdAt(product.getMeta().getCreatedAt())
                    .updatedAt(product.getMeta().getUpdatedAt())
                    .barcode(product.getMeta().getBarcode())
                    .qrCode(product.getMeta().getQrCode())
                    .build();
        }

        // Konversi Images
        List<String> imageUrls = product.getImages() != null
                ? product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .toList()
                : List.of();

        // Build ProductDto
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .rating(product.getRating())
                .stock(product.getStock())
                .tags(product.getTags() != null ? List.copyOf(product.getTags()) : List.of())
                .brand(product.getBrand())
                .sku(product.getSku())
                .weight(product.getWeight())
                .dimensions(dimensionDto)
                .warrantyInformation(product.getWarrantyInformation())
                .shippingInformation(product.getShippingInformation())
                .availableStatus(product.getAvailabilityStatus().getDisplayName())
                .reviews(reviewDtos)
                .returnPolicy(product.getReturnPolicy())
                .minimumOrderQuantity(product.getMinimumOrderQuantity())
                .meta(metaDto)
                .images(imageUrls)
                .thumbnail(product.getThumbnail())

                .build();
    }

}
