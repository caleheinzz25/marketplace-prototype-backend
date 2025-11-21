package EzyShop.mapper;

import EzyShop.dto.payment.ChannelProperties;
import EzyShop.dto.payment.PaymentRequest;
import EzyShop.exception.BusinessException;
import EzyShop.model.orders.ChannelCode;
import EzyShop.utils.CartUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;

public class MapperEWallet {

        private static final String RETURN_URL = "https://caleheinzz.my.id/order/details?ref=";

        public static PaymentRequest GopayRequestToDto(PaymentRequest input) {
                BigDecimal totalAmount = CartUtils.calculateGrandTotal(input.getCarts(), input.getTax(),
                                input.getShippingType().getCost());
                if (totalAmount.compareTo(new BigDecimal("50000000")) > 0) {
                        throw new BusinessException("Gopay request amount cannot exceed 50,000,000 IDR",
                                        HttpStatus.BAD_REQUEST);
                }
                String refId = generateReferenceIdDev();
                return PaymentRequest.builder()
                                .referenceId(refId)
                                .type(input.getType())
                                .country("ID")
                                .currency("IDR")
                                .channelCode(ChannelCode.GOPAY)
                                .requestAmount(CartUtils.calculateGrandTotal(input.getCarts(), input.getTax(),
                                                input.getShippingType().getCost()))
                                .channelProperties(ChannelProperties.builder()
                                                .successReturnUrl(RETURN_URL + refId)
                                                .failureReturnUrl(RETURN_URL + refId)
                                                .cancelReturnUrl(RETURN_URL + refId)
                                                .build())
                                .build();
        }

        public static PaymentRequest DanaRequestToDto(PaymentRequest input) {
                BigDecimal totalAmount = CartUtils.calculateGrandTotal(input.getCarts(), input.getTax(),
                                input.getShippingType().getCost());
                if (totalAmount.compareTo(new BigDecimal("20000000")) > 0) {
                        throw new BusinessException("Gopay request amount cannot exceed 20,000,000 IDR",
                                        HttpStatus.BAD_REQUEST);
                }
                String refId = generateReferenceIdDev();
                return PaymentRequest.builder()
                                .referenceId(refId)
                                .type(input.getType())
                                .country("ID")
                                .currency("IDR")
                                .channelCode(ChannelCode.DANA)
                                .requestAmount(totalAmount)
                                .channelProperties(ChannelProperties.builder()
                                                .successReturnUrl(RETURN_URL + refId)
                                                .build())
                                .build();
        }

        public static PaymentRequest QrisRequestToDto(PaymentRequest input) {
                String refId = generateReferenceIdDev();
                return PaymentRequest.builder()
                                .referenceId(refId)
                                .type(input.getType())
                                .country("ID")
                                .currency("IDR")
                                .channelCode(ChannelCode.QRIS)
                                .requestAmount(CartUtils.calculateGrandTotal(input.getCarts(), input.getTax(),
                                                input.getShippingType().getCost()))
                                .build();
        }

        private static Instant getExpiresAt() {
                return Instant.now().plus(24, ChronoUnit.HOURS);
        }

        private static String generateReferenceIdDev() {
                long timestamp = Instant.now().getEpochSecond();
                return "test-" + timestamp;
        }

        // private static String generateReferenceId() {
        //         long timestamp = Instant.now().getEpochSecond();
        //         return "ORD-" + timestamp;
        // }
}
