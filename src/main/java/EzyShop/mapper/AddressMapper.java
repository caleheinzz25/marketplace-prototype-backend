package EzyShop.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import EzyShop.dto.User.AddressDto;
import EzyShop.model.Address;
import EzyShop.model.User;
import EzyShop.model.orders.ShippingInfoSnapshot;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "store.storeName", target = "storeName")
    @Mapping(source = "store.storeType", target = "storeType")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "store.id", target = "storeId")
    AddressDto toDto(Address address);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "store", ignore = true)
    Address toEntity(AddressDto addressDto);

    List<AddressDto> toDtoList(List<Address> addresses);

    List<Address> toEntityList(List<AddressDto> addressDtos);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "store", ignore = true)
    void updateEntityFromDto(AddressDto dto, @MappingTarget Address entity);

    default Address toEntity(AddressDto dto, User user) {
        Address address = toEntity(dto);
        address.setUser(user);
        return address;
    }

    default ShippingInfoSnapshot toShippingInfoSnapshot(Address address) {
        if (address == null) {
            return null;
        }

        return ShippingInfoSnapshot.builder()
                .recipientFirstName(address.getFirstName())
                .recipientLastName(address.getLastName())
                .phoneNumber(address.getPhoneNumber())
                .streetAddress(address.getStreetAddress())
                .apartment(address.getApartment())
                .city(address.getCity())
                .stateProvince(address.getStateProvince())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }
}
