package EzyShop.mapper;

import org.mapstruct.Mapper;
import EzyShop.dto.product.ReviewDto;
import EzyShop.model.products.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDto toDto(Review review);
    Review toEntity(ReviewDto dto);
}
