package EzyShop.service;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import EzyShop.dto.User.AddressDto;
import EzyShop.exception.BusinessException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.mapper.AddressMapper;
import EzyShop.model.Address;
import EzyShop.model.User;
import EzyShop.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public List<AddressDto> getAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addressMapper.toDtoList(addresses);
    }

    public AddressDto createAddressByUserId(Long userId, AddressDto dto) {
        if (dto == null) throw new BusinessException("Address data must not be null",HttpStatus.BAD_REQUEST);
        User user = User.builder().id(userId).build();
        Address address = addressMapper.toEntity(dto, user);
        Address saved = addressRepository.save(address);
        return addressMapper.toDto(saved);
    }

    public AddressDto updateAddressByUserId(Long addressId, AddressDto dto) {
        if (dto == null) throw new BusinessException("Address data must not be null", HttpStatus.BAD_REQUEST);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address with ID " + addressId + " not found"));

        addressMapper.updateEntityFromDto(dto, address);
        Address updated = addressRepository.save(address);
        return addressMapper.toDto(updated);
    }

    public void setDefaultShippingAddress(Long userId, Long addressId) {
        List<Address> addresses = addressRepository.findByUserId(userId);

        boolean addressFound = false;
        for (Address address : addresses) {
            if (address.getId().equals(addressId)) {
                address.setDefaultShipping(true);
                addressFound = true;
            } else if (address.isDefaultShipping()) {
                address.setDefaultShipping(false);
            }
        }

        if (!addressFound) {
            log.warn("Default address not set: Address ID {} not found for user {}", addressId, userId);
            throw new ResourceNotFoundException("Address not found or unauthorized");
        }

        addressRepository.saveAll(addresses);
        log.info("Default shipping address set to ID {} for user {}", addressId, userId);
    }

    public void deleteAddress(Long id) {
        try {
            addressRepository.deleteById(id);
            log.info("Deleted address with ID {}", id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Attempted to delete non-existing address with ID {}", id);
            throw new ResourceNotFoundException("Address not found");
        }
    }

    public List<AddressDto> getAddressAdmin() {
        List<Address> addresses = addressRepository.findAll();
        return addressMapper.toDtoList(addresses);
    }
}
