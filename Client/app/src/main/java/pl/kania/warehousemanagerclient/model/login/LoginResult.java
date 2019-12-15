package pl.kania.warehousemanagerclient.model.login;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResult {
    private String token;
    private String login;
    private String errorMessage;
    private boolean manager;
}
