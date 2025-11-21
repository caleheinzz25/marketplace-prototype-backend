package EzyShop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import EzyShop.dto.order.TransactionDto;
import EzyShop.service.TransactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{ref}")
    public ResponseEntity<?> getTransactionByRefId(@PathVariable String ref) {
        TransactionDto transactionDto =  transactionService.getTransactionByreferenceId(ref);
        return ResponseEntity.ok().body(transactionDto);
    }
    
}
