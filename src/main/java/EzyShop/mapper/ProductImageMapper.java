package EzyShop.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import EzyShop.model.products.ProductImage;
import EzyShop.dto.product.ProductImageDto;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageDto toDto(ProductImage entity);

    @Mapping(ignore = true, target = "product")
    ProductImage toEntity(ProductImageDto dto);

    default List<String> toDtoList(List<ProductImage> images) {
        return images.stream()
                .map(ProductImage::getImageUrl)
                .toList();
    }

    default List<ProductImage> toEntityList(List<String> imageUrls) {
        return imageUrls.stream()
                .map(url -> {
                    ProductImage image = new ProductImage();
                    image.setImageUrl(url);
                    return image;
                })
                .collect(Collectors.toList());
    }
}
