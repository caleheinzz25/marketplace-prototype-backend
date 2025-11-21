package EzyShop.mapper;

import org.mapstruct.*;
import EzyShop.model.carts.CartItem;
import EzyShop.dto.cart.CartItemDto;
import EzyShop.model.products.Product;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mappings({
            @Mapping(source = "product.id", target = "productId"),
            @Mapping(source = "product.title", target = "productTitle"),
            @Mapping(source = "product.brand", target = "brand"),
            @Mapping(source = "product.stock", target = "maxStock"),
            @Mapping(source = "product.sku", target = "productSku"),
            @Mapping(source = "product.thumbnail", target = "productThumbnail"),
            @Mapping(source = "product.store.storeName", target = "storeName"),
            @Mapping(source = "priceSnapshot", target = "price"),
            @Mapping(expression = "java(item.getSubtotal())", target = "subtotal")
    })
    CartItemDto toDto(CartItem item);

    @Mappings({
            @Mapping(target = "cart", ignore = true), // karena biasanya akan di-set dari Cart.addItem
            @Mapping(source = "price", target = "priceSnapshot"),
            @Mapping(target = "product", expression = "java(mapProduct(dto.getProductId()))")
    })
    CartItem toEntity(CartItemDto dto);

    default Product mapProduct(Long productId) {
        if (productId == null)
            return null;
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
