package pl.kania.warehousemanager.beans;

import org.springframework.stereotype.Component;
import pl.kania.warehousemanager.model.UserCredentials;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
public class HeaderExtractor {

    public Optional<UserCredentials> extractCredentialsFromAuthorizationHeader(String header, List<String> errors) {
            final int indexOfSpace = header.indexOf(' ');
            if (indexOfSpace == -1) {
                errors.add("Token not found in authorization header");
                return Optional.empty();
            }
            final String decoded = new String(Base64.getDecoder().decode(header.substring(indexOfSpace).trim()));
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

    public Optional<String> extractTokenFromAuthorizationHeader(String header, List<String> errors) {
        final int indexOfSpace = header.indexOf(" ");
        if (indexOfSpace == -1) {
            errors.add("Token not found in authorization header");
            return Optional.empty();
        }
        final String token = header.substring(indexOfSpace + 1).trim();
        return Optional.of(token);
    }
}
