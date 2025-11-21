package EzyShop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import EzyShop.dto.User.AddressDto;
import EzyShop.service.AddressService;
import EzyShop.utils.JWTUtils;
import EzyShop.utils.ParsedJWT;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressService addressService;
    private final JWTUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getAddresses(@RequestHeader("Authorization") String authToken) {
        ParsedJWT token = jwtUtils.extractAllClaims(authToken.substring(7));
        Long userId = token.getUserId();

        List<AddressDto> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(Map.of("addresses", addresses));
    }

    @PostMapping
    public ResponseEntity<?> createAddress(
            @RequestHeader("Authorization") String authToken,
            @Valid @RequestBody AddressDto addressDto) {
        ParsedJWT token = jwtUtils.extractAllClaims(authToken.substring(7));
        Long userId = token.getUserId();

        addressService.createAddressByUserId(userId, addressDto);
        return ResponseEntity.ok(Map.of("message", "Address created successfully"));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(
            @RequestHeader("Authorization") String authToken,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDto addressDto) {
        addressService.updateAddressByUserId(addressId, addressDto);
        return ResponseEntity.ok(Map.of("message", "Address updated successfully"));
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<?> setDefaultAddress(
            @RequestHeader("Authorization") String authToken,
            @PathVariable Long addressId) {

        ParsedJWT token = jwtUtils.extractAllClaims(authToken.substring(7));
        Long userId = token.getUserId();
        log.info("{}", userId);
        addressService.setDefaultShippingAddress(userId, addressId);
        return ResponseEntity.ok(Map.of("message", "Default shipping address updated successfully"));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @RequestHeader("Authorization") String authToken,
            @PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('management:read')")
    public ResponseEntity<?> getAddressesAdmin() {
        List<AddressDto> addresses = addressService.getAddressAdmin();
        return ResponseEntity.ok(addresses);
    }

}
