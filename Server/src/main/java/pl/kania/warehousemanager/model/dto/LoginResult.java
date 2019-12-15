package pl.kania.warehousemanager.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResult {
    private String token;
    private String login;
    private String errorMessage;
    private boolean manager;

    public LoginResult(String token, String login, boolean manager) {
        this.token = token;
        this.login = login;
        this.manager = manager;
    }
}
