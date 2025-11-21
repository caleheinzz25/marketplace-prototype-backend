package EzyShop.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import EzyShop.dto.store.StoreDto;
import EzyShop.dto.store.StoreRegistrationRequest;
import EzyShop.exception.AuthenticationException;
import EzyShop.exception.DuplicateResourceException;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.exception.UnauthorizedException;
import EzyShop.model.Role;
import EzyShop.model.User;
import EzyShop.model.store.Store;
import EzyShop.repository.StoreRepository;
import EzyShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreDto getStore(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found Please Login first"));

        Store store = storeRepository.findById(user.getStore().getId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found Please Register Your Store first"));
        return toStoreDto(store);
    }

    @Transactional
    public StoreDto addStore(Long userId, StoreDto dto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Store store = Store.builder()
                .storeName(dto.getStoreName())
                .logUrl(dto.getLogUrl())
                .description(dto.getDescription())
                .storeType(dto.getStoreType())
                .saldo(Optional.ofNullable(dto.getSaldo()).orElse(BigDecimal.ZERO))
                .owner(owner)
                .build();

        Store savedStore = storeRepository.save(store);
        return toStoreDto(savedStore);
    }

    @Transactional
    public StoreDto updateStore(Long storeId, StoreDto dto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        store.setStoreName(dto.getStoreName());
        store.setLogUrl(dto.getLogUrl());
        store.setDescription(dto.getDescription());
        store.setSaldo(Optional.ofNullable(dto.getSaldo()).orElse(store.getSaldo()));
        store.setStoreType(dto.getStoreType());
        Store updated = storeRepository.save(store);
        return toStoreDto(updated);
    }

    @Transactional
    public void removeStore(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store not found");
        }
        storeRepository.deleteById(storeId);
    }

    public StoreDto toStoreDto(Store store) {
        return StoreDto.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .logUrl(store.getLogUrl())
                .description(store.getDescription())
                .saldo(store.getSaldo())
                .storeType(store.getStoreType())
                .ownerUsername(store.getOwner().getUsername())
                .build();
    }




}
