package EzyShop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import EzyShop.dto.payment.PaymentRequest;
import EzyShop.dto.payment.PaymentResponse;
import EzyShop.service.PaymentService;
import EzyShop.utils.JWTUtils;
import EzyShop.utils.ParsedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final JWTUtils jwtUtils;

    @PostMapping("/DANA")
    public ResponseEntity<?> dana(@RequestBody PaymentRequest danaPaymentInput,
            @RequestHeader("Authorization") String authToken) {
        log.info(authToken);
        ParsedJWT token = jwtUtils.extractAllClaims(authToken.substring(7));
        Long userId = token.getUserId();
        PaymentResponse response = paymentService.createDANAPayment(danaPaymentInput, userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/GOPAY")
    public ResponseEntity<?> gopay(@RequestBody PaymentRequest gopayPaymentRequest,
            @RequestHeader("Authorization") String authToken) {
        log.info(authToken);
        ParsedJWT token = jwtUtils.extractAllClaims(authToken.substring(7));
        Long userId = token.getUserId();
        PaymentResponse response = paymentService.createGopayPayment(gopayPaymentRequest, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/QRIS")
    public ResponseEntity<PaymentResponse> createQris(@RequestBody PaymentRequest qrisPaymentRequest,
            @RequestHeader("Authorization") String authToken) {
        log.info(authToken);
        ParsedJWT token = jwtUtils.extractAllClaims(authToken.substring(7));
        Long userId = token.getUserId();
        return ResponseEntity.ok(paymentService.createQrisPayment(qrisPaymentRequest, userId));
    }

    @PostMapping("/BCA_VIRTUAL_ACCOUNT")
    public ResponseEntity<PaymentResponse> postMethodName(@RequestBody PaymentRequest bcaVaPaymentInput,
            @RequestHeader("Authorization") String authToken) {
        log.info(authToken);
        ParsedJWT token = jwtUtils.extractAllClaims(authToken.substring(7));
        Long userId = token.getUserId();
        // TODO: process POST request

        return ResponseEntity.ok(paymentService.createBcaVaPayment(bcaVaPaymentInput, userId));
    }

}
