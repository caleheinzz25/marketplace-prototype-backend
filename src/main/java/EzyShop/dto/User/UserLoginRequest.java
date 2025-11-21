package EzyShop.dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserLoginRequest {
    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, max = 100, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 30, message = "Username harus antara 3 hingga 30 karakter")
    private String username;
}
