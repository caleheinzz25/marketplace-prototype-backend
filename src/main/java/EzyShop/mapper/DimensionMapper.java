package EzyShop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import EzyShop.model.products.Dimension;
import EzyShop.dto.product.DimensionDto;

@Mapper(componentModel = "spring")
public interface DimensionMapper {
    DimensionDto toDto(Dimension dimension);

    Dimension toEntity(DimensionDto dto);
}
