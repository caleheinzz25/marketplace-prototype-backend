package EzyShop.mapper;

import org.mapstruct.*;
import java.util.List;

import EzyShop.model.carts.Cart;
import EzyShop.dto.cart.CartDto;
import EzyShop.model.User;
import EzyShop.model.store.Store;

@Mapper(componentModel = "spring", uses = {
    CartItemMapper.class
})
public interface CartMapper {

    @Mappings({
        @Mapping(source = "user.id", target = "userId"),
        @Mapping(source = "store.id", target = "storeId"),
        @Mapping(target = "total", expression = "java(cart.getTotal())"),
    })
    CartDto toDto(Cart cart);

    @Mappings({
        @Mapping(target = "user", expression = "java(mapUser(dto.getUserId()))"),
        @Mapping(target = "store", expression = "java(mapStore(dto.getStoreId()))"),
        @Mapping(target = "items", ignore = true), // handled after cart creation
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Cart toEntity(CartDto dto);

    List<CartDto> toListDto(List<Cart> carts);

    // Manual mapper helpers
    default User mapUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Store mapStore(Long storeId) {
        if (storeId == null) return null;
        Store store = new Store();
        store.setId(storeId);
        return store;
    }
}
