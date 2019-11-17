package pl.kania.warehousemanager.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResult {
    private String token;
    private String login;
    private String errorMessage;

    public LoginResult(String token, String login) {
        this.token = token;
        this.login = login;
    }
}
