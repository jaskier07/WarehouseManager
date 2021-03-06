package pl.kania.warehousemanagerclient.model.dto;

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
}
