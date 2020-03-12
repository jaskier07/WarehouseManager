package pl.kania.warehousemanager.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.services.beans.HeaderExtractor;

import java.util.Optional;

@Slf4j
@Service
public class RoleChecker {

    @Autowired
    private HeaderExtractor headerExtractor;

    public boolean hasRole(WarehouseRole requiredRole, String header) {
        try {
            final Optional<String> token = headerExtractor.extractTokenFromAuthorizationHeader(header);
            if (!token.isPresent()) {
                return false;
            }
            final DecodedJWT decodedToken = JWT.decode(token.get());
            final Claim role = decodedToken.getClaim("role");
            if (role.isNull()) {
                return false;
            }
            WarehouseRole userRole = WarehouseRole.valueOf(role.asString());
            if (requiredRole == WarehouseRole.MANAGER) {
                return userRole == WarehouseRole.MANAGER;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (TokenNotFoundInHeaderException t) {
            log.error(t.getMessage());
            return false;
        }
    }

}
