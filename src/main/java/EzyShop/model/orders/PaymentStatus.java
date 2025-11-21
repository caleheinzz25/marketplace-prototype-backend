    package EzyShop.model.orders;

    /**
     * Enum untuk merepresentasikan status pembayaran dalam sistem.
     * Cocok untuk digunakan bersama integrasi Xendit atau sistem pembayaran lain.
     */
    public enum PaymentStatus {

        /**
         * Status awal sebelum pembayaran dibuat atau dikirim ke gateway.
         */
        PENDING,

        /**
         * Pembayaran dibuat dan menunggu aksi dari customer (misal redirect ke payment page).
         * Umumnya muncul secara sinkron setelah request awal.
         */
        REQUIRES_ACTION,

        /**
         * Pembayaran berhasil diselesaikan.
         * Dideteksi dari webhook seperti `payment.capture`.
         */
        SUCCEEDED,

        /**
         * Pembayaran gagal (misal karena saldo tidak cukup, kartu ditolak, dll).
         * Dideteksi dari webhook seperti `payment.failure`.
         */
        FAILED,

        /**
         * Pembayaran dibatalkan oleh sistem atau user sebelum diselesaikan.
         * Biasanya hanya valid dari status `REQUIRES_ACTION`.
         */
        CANCELED,

        /**
         * Pembayaran kedaluwarsa karena tidak ada aksi dari user.
         * Dideteksi dari webhook seperti `payment_request.expiry`.
         */
        EXPIRED
    }
