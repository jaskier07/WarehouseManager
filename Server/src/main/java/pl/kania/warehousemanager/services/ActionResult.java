package pl.kania.warehousemanager.services;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionResult<T> {
    T result;
    String errorMessage;

    public ActionResult(T result) {
        this.result = result;
    }

    public ActionResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }

    public boolean isError() {
        return !isSuccess();
    }
}
