package EzyShop.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import EzyShop.dto.cart.CartDto;
import EzyShop.dto.invoice.InvoiceDto;
import EzyShop.dto.order.OrderDto;
import EzyShop.service.OrderService;
import EzyShop.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JWTUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getOrdersByUserId(@RequestHeader("Authorization") String authToken) {
        Long userId = jwtUtils.extractUserId(authToken.substring(7));
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/invoices/by-number/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceByInvoiceNumber(@PathVariable String invoiceNumber) {
        InvoiceDto invoice = orderService.getInvoiceByInvoiceNumber(invoiceNumber);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/invoices/by-order-id/{orderId}")
    public ResponseEntity<?> getInvoiceByOrderId(@PathVariable Long orderId) {
        InvoiceDto invoice = orderService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/{ref}")
    public ResponseEntity<OrderDto> getOrderByRef(@PathVariable String ref) {
        return ResponseEntity.ok(orderService.getOrderByReferenceId(ref));
    }

    @GetMapping("/store")
    public ResponseEntity<?> getOrdersByStore(@RequestHeader("Authorization") String authToken) {
        Long userId = jwtUtils.extractUserId(authToken.substring(7));

        List<OrderDto> orders = orderService.getOrdersByStoreId(userId);
        return ResponseEntity.ok().body(orders);
    }

    @GetMapping("/store/{limit}")
    public ResponseEntity<?> getOrdersByStoreLimit(@RequestHeader("Authorization") String authToken, @PathVariable Integer limit) {
        Long userId = jwtUtils.extractUserId(authToken.substring(7));

        List<OrderDto> orders = orderService.getOrdersByStoreIdAndLimit(userId, limit);
        return ResponseEntity.ok().body(orders);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String authToken,
            @RequestBody List<CartDto> carts) {
        // TODO: process POST request

        Long userId = jwtUtils.extractUserId(authToken.substring(7));
        OrderDto order = orderService.createOrder(userId, carts);
        return ResponseEntity.ok(order);
    }

}
