package EzyShop.mapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

import EzyShop.dto.payment.ChannelProperties;
import EzyShop.dto.payment.PaymentRequest;
import EzyShop.model.orders.ChannelCode;
import EzyShop.utils.CartUtils;

public class MapperBank {
    public static PaymentRequest mapToBcaVaPaymentRequest(PaymentRequest input) {
        return PaymentRequest.builder()
                .referenceId(generateReferenceId())
                .type(input.getType())
                .country("ID")
                .currency("IDR")
                .requestAmount(CartUtils.calculateGrandTotal(input.getCarts(), input.getTax(),
                        input.getShippingType().getCost()))
                .channelCode(ChannelCode.BCA_VIRTUAL_ACCOUNT)
                .channelProperties(ChannelProperties.builder()
                        .displayName(sanitizeDisplayName(input.getChannelProperties().getDisplayName()))
                        .expiresAt(getExpiresAt())
                        .build())
                .description(input.getDescription())
                .metadata(input.getMetadata())
                .build();
    }

    private static Instant getExpiresAt() {
        return Instant.now().plus(24, ChronoUnit.HOURS);
    }

    private static String generateReferenceId() {
        // Simulasi pengganti {{$timestamp}} → bisa disesuaikan
        long timestamp = Instant.now().getEpochSecond();
        return "test-" + timestamp;
    }

    private static final Pattern VALID_PATTERN = Pattern.compile("^(?! )[a-zA-Z0-9 .,']+$");

    public static String sanitizeDisplayName(String input) {
        if (input == null)
            return "";

        // Hapus karakter tidak valid: hanya izinkan a-zA-Z0-9 spasi titik koma apostrof
        String sanitized = input.replaceAll("[^a-zA-Z0-9 .,']", "").trim();

        // Hapus spasi di awal jika masih ada
        while (sanitized.startsWith(" ")) {
            sanitized = sanitized.substring(1);
        }

        // Validasi akhir
        if (VALID_PATTERN.matcher(sanitized).matches()) {
            return sanitized;
        } else {
            throw new IllegalArgumentException("Display name tidak valid setelah sanitasi: " + sanitized);
        }
    }
}
