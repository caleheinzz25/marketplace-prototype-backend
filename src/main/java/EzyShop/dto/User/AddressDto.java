package EzyShop.dto.User;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    private Long id;

    @NotBlank(message = "Tipe alamat tidak boleh kosong")
    private String addressType; // Bisa pakai enum jika dibatasi

    @NotBlank(message = "Nama depan tidak boleh kosong")
    private String firstName;

    @NotBlank(message = "Nama belakang tidak boleh kosong")
    private String lastName;

    @NotBlank(message = "Alamat jalan tidak boleh kosong")
    private String streetAddress;

    private String apartment; // Optional

    @NotBlank(message = "Kota tidak boleh kosong")
    private String city;

    private String stateProvince;

    @NotBlank(message = "Kode pos tidak boleh kosong")
    private String postalCode;

    @NotBlank(message = "Negara tidak boleh kosong")
    private String country;

    @Pattern(regexp = "^\\+?\\d{8,15}$", message = "Format nomor telepon tidak valid")
    private String phoneNumber;
    private Long userId; // hanya ID, bukan object
    private Long storeId; // hanya ID, bukan object

    // tambahan informasi Store (read-only dari mapper)
    private String storeName;
    private String storeType;
    private boolean defaultShipping;
}
