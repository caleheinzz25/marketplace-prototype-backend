package EzyShop.dto.payment;

public enum PaymentChannel {
    QRIS,
    VA,
    EWALLET,
    CARD;

    public static boolean isSupported(String channelCode) {
        try {
            PaymentChannel.valueOf(channelCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
