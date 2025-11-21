package EzyShop.mapper;

import EzyShop.dto.order.TransactionDto;
import EzyShop.model.orders.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { OrderMapper.class })
public interface TransactionMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "shippingInfo", target = "shippingAddress")
    TransactionDto toDto(Transaction tx);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orders", ignore = true) // diisi manual jika perlu
    @Mapping(target = "shippingInfo", source = "shippingAddress")
    Transaction toEntity(TransactionDto dto);

    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "userId", ignore = true)
    TransactionDto toTransactionDto(Transaction transaction);

    List<Transaction> toEntityList(List<TransactionDto> dtoList);

    default List<TransactionDto> toDtoList(List<Transaction> dtoList) {
        return dtoList.stream()
                .map(this::toDto)
                .toList(); // Java 16+, gunakan .collect(Collectors.toList()) jika lebih lama
    }
}
