package EzyShop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import EzyShop.dto.product.ListProductRequest;
import EzyShop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Slf4j
@RestController
@RequestMapping("/api/v1/save-bulk")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('admin:create')")
public class SaveBulk {

    private final ProductService productService;

    @PostMapping("/bulk")
    public ResponseEntity<?> createBulkProducts(@RequestBody ListProductRequest productRequest) {
        log.info(productRequest.toString());
        productService.saveBulkProducts(productRequest);
        return ResponseEntity.ok(Map.of("message", "Products created successfully"));
    }


    

}
