package EzyShop.dto.User;

import EzyShop.dto.store.StoreDto;
import EzyShop.model.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 30, message = "Username harus antara 3 hingga 30 karakter")
    private String username;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama lengkap harus antara 3 hingga 100 karakter")
    private String fullName;

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Pattern(regexp = "^\\+?\\d{8,15}$", message = "Format nomor telepon tidak valid")
    private String phoneNumber;

    private Boolean enabled;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, max = 100, message = "Password minimal 6 karakter")
    private String password;

    @NotNull(message = "Role harus dipilih")
    private Role role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private StoreDto store;
}
