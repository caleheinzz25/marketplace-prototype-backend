package EzyShop.service;

import org.springframework.stereotype.Service;

import EzyShop.dto.order.TransactionDto;
import EzyShop.exception.ResourceNotFoundException;
import EzyShop.mapper.OrderMapper;
import EzyShop.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionDto getTransactionByreferenceId(String referenceId) {
        return OrderMapper.toTransactionDto(transactionRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new  ResourceNotFoundException("Transaction not found with referenceId: " + referenceId)));
    }
}
