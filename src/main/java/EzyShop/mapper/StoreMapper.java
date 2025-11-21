package EzyShop.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import EzyShop.dto.store.StoreDto;
import EzyShop.model.store.Store;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(source = "owner.username", target = "ownerUsername")
    StoreDto toDto(Store store);

    @Mapping(source = "ownerUsername", target = "owner.username")
    Store toEntity(StoreDto dto);

    List<StoreDto> toDtoList(List<Store> stores);
}
