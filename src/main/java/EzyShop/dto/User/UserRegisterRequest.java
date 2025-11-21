package EzyShop.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 30, message = "Username harus antara 3 hingga 30 karakter")
    private String username;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;
    
    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama lengkap harus antara 3 hingga 100 karakter")
    private String fullName;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, max = 100, message = "Password minimal 6 karakter")
    private String password;
}
