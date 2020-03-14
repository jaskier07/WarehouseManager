package pl.kania.warehousemanager.services.beans;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;
import pl.kania.warehousemanager.model.UserCredentials;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderExtractor {

    public static Optional<UserCredentials> extractCredentialsFromAuthorizationHeader(String header, List<String> errors) throws TokenNotFoundInHeaderException {
        Optional<String> token = extractTokenFromAuthorizationHeader(header);
        if (!token.isPresent()) {
            return Optional.empty();
        }
        final String decoded = new String(Base64.getDecoder().decode(token.get()));
        if (!decoded.contains(".")) {
            errors.add("Token does not contain parts separated by dot");
            return Optional.empty();
        }
        final String[] parts = decoded.split("\\.");
        if (parts.length == 2) {
            return Optional.of(new UserCredentials(parts[0], parts[1]));
        }
        errors.add("Token does not contain two parts");
        return Optional.empty();
    }

    public static Optional<String> extractTokenFromAuthorizationHeader(String header) throws TokenNotFoundInHeaderException {
        final int indexOfSpace = header.indexOf(" ");
        if (indexOfSpace == -1) {
            throw new TokenNotFoundInHeaderException();
        }
        final String token = header.substring(indexOfSpace + 1).trim();
        return Optional.of(token);
    }
}
