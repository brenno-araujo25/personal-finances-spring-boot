package io.github.brenno_araujo25.personal_finances.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String username;
    private String password;

}
