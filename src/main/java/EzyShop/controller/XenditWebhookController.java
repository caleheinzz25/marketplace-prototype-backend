package EzyShop.controller;

import EzyShop.dto.webhook.XenditPaymentWebhook;
import EzyShop.service.WebhookService;
// import EzyShop.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/webhook/xendit")
public class XenditWebhookController {

    private static final String CALLBACK_SECRET = "5cpoXoEf3gDs9iwy8Cs2XnnNpNXeCvEM4sPBgtpemSRPbMgU";
    private final WebhookService webhookService;

    @GetMapping("/test")
    public String getMethodName() {
        return new String("Hello World");
    }

    @PostMapping("/payment_token")
    public ResponseEntity<Void> tokenokenActivationStatus(
            @RequestHeader("X-CALLBACK-TOKEN") String token,
            @RequestBody XenditPaymentWebhook payload) {

        log.info(token);
        log.info(payload.toString());
        if (!token.equals(CALLBACK_SECRET)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Proses async agar tidak delay balasan ke Xendit
        // xenditWebhookService.handleWebhookAsync(payload);

        // LANGSUNG balas tanpa tunggu pemrosesan
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment_request_status")
    public String paymentRequestStatus(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    
    @PostMapping("/payment_status")
    public ResponseEntity<?> paymentCapture(@RequestBody XenditPaymentWebhook entity) {
        //TODO: process POST request
        webhookService.handlePaymentWebhook(entity);
        return ResponseEntity.ok().build();
    }
    
}   
