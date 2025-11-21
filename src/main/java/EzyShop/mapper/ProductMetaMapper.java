package EzyShop.mapper;

import org.mapstruct.Mapper;
import EzyShop.dto.product.MetaDto;
import EzyShop.model.products.ProductMeta;

@Mapper(componentModel = "spring")
public interface ProductMetaMapper {
    MetaDto toDto(ProductMeta meta);
    ProductMeta toEntity(MetaDto dto);
}
