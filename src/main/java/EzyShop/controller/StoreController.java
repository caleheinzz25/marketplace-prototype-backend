package EzyShop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import EzyShop.dto.store.StoreDto;
import EzyShop.service.StoreService;
import EzyShop.utils.JWTUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final JWTUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getMethodName(@RequestHeader("Authorization") String authToken) {
        Long userId = jwtUtils.extractUserId(authToken.substring(7));

        StoreDto storeDto = storeService.getStore(userId);

        return ResponseEntity.ok().body(storeDto);
    }

}
