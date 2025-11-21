package EzyShop.dto.store;

import jakarta.validation.constraints.*;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRegistrationRequest {

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Pattern(regexp = "^\\+?\\d{8,15}$", message = "Format nomor telepon tidak valid")
    private String contactPhone;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 30, message = "Username harus antara 3 hingga 30 karakter")
    private String username;

    @NotBlank(message = "Email toko tidak boleh kosong")
    @Email(message = "Format email toko tidak valid")
    private String storeEmail;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, max = 100, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Konfirmasi password tidak boleh kosong")
    private String confirmPassword;

    @Size(max = 255, message = "Deskripsi maksimal 255 karakter")
    private String description;

    @NotBlank(message = "Nama toko tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama toko harus antara 3 hingga 100 karakter")
    private String storeName;

    @NotBlank(message = "Jenis toko tidak boleh kosong")
    private String storeType;

    @AssertTrue(message = "Anda harus menyetujui syarat dan ketentuan")
    private boolean terms;
}
