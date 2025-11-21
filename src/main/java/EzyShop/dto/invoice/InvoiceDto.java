package EzyShop.dto.invoice;

import EzyShop.model.orders.ChannelCode;
import EzyShop.model.orders.Invoice;
import EzyShop.model.orders.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class InvoiceDto {
    private String invoiceNumber;
    private String buyerName;
    private ChannelCode paymentMethod;
    private OrderStatus status;
    private BigDecimal totalAmount;

    private Instant issuedAt;     
    private Instant paidAt;       

    private List<InvoiceItemDto> items;

    public static InvoiceDto fromEntity(Invoice invoice) {
        return InvoiceDto.builder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .buyerName(invoice.getBuyerName())
                .paymentMethod(invoice.getPaymentMethod())
                .status(invoice.getStatus())
                .totalAmount(invoice.getTotalAmount())
                .issuedAt(invoice.getIssuedAt())  // ⬅️ Sekarang sama-sama Instant
                .paidAt(invoice.getPaidAt())
                .items(invoice.getItems().stream()
                        .map(InvoiceItemDto::fromEntity)
                        .toList())
                .build();
    }
}
