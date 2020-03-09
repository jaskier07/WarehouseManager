package pl.kania.warehousemanager.exceptions;

public class TokenNotFoundInHeaderException extends Exception {

    public static final String MESSAGE = "Token not found in authorization header";

    public TokenNotFoundInHeaderException() {
        super(MESSAGE);
    }
}
