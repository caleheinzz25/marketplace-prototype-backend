package EzyShop.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import EzyShop.dto.product.ProductDto;
import EzyShop.model.products.Product;

@Mapper(componentModel = "spring", uses = {
        DimensionMapper.class,
        ReviewMapper.class,
        ProductMetaMapper.class,
        ProductImageMapper.class
})
public interface ProductMapper {

    @Mappings({
            @Mapping(source = "meta", target = "meta"),
            @Mapping(source = "dimensions", target = "dimensions"),
            @Mapping(source = "store.id", target = "storeId"),
            @Mapping(source = "images", target = "images"),
            @Mapping(source = "reviews", target = "reviews"),
            @Mapping(target = "enabled", source = "enabled") // fix mapping name
    })
    ProductDto toDto(Product product);

    @Mappings({
            @Mapping(target = "store.id", source = "storeId"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "images", source = "images"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updateAt", ignore = true),
            @Mapping(target = "enabled", source = "enabled")
    })
    Product toEntity(ProductDto dto);

    // Mengembalikan semua, termasuk yang disable
    default List<ProductDto> toListDtoAll(List<Product> products) {
        return products.stream()
                .map(this::toDto)
                .toList();
    }

    // Mengembalikan hanya produk yang isEnabled = true
    default List<ProductDto> toListDtoOnlyEnabled(List<Product> products) {
        return products.stream()
                .filter(this::isProductEnabled)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    default boolean isProductEnabled(Product product) {
        return product != null && Boolean.TRUE.equals(product.getEnabled());
    }

    // Logic: kembalikan null jika isEnabled = false
    default ProductDto safeToDto(Product product) {
        if (!isProductEnabled(product)) {
            return null;
        }
        return toDto(product);
    }

    default void updateEntityFromDto(ProductDto productDto, Product product) {
        Product productUpdate = toEntity(productDto);
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setRating(productDto.getRating());
        product.setStock(productDto.getStock());
        product.setTags(productDto.getTags());
        product.setBrand(productDto.getBrand());
        product.setImages(product.getImages());
        product.setSku(productDto.getSku());
        product.setWeight(productDto.getWeight());
        product.setWarrantyInformation(productDto.getWarrantyInformation());
        product.setShippingInformation(productDto.getShippingInformation());
        product.setAvailabilityStatus(productDto.getAvailabilityStatus());
        product.setReturnPolicy(productDto.getReturnPolicy());
        product.setMinimumOrderQuantity(productDto.getMinimumOrderQuantity());
        product.setThumbnail(productDto.getThumbnail());
    }
}
