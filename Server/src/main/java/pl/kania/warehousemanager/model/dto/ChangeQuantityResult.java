package pl.kania.warehousemanager.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangeQuantityResult {
    private Boolean changeSuccessful;
    private String error;

    public ChangeQuantityResult(Boolean changeSuccessful) {
        this.changeSuccessful = changeSuccessful;
    }

    public ChangeQuantityResult(Boolean changeSuccessful, String error) {
        this.changeSuccessful = changeSuccessful;
        this.error = error;
    }
}
