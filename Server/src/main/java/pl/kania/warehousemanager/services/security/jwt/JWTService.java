package pl.kania.warehousemanager.services.security.jwt;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.warehousemanager.model.WarehouseRole;
import pl.kania.warehousemanager.model.db.ClientDetails;
import pl.kania.warehousemanager.model.db.User;
import pl.kania.warehousemanager.services.ActionResult;

import java.util.Optional;

@Slf4j
@Service
public class JWTService {

    private PermissionChecker permissionChecker;
    private Environment environment;
    private GoogleTokenVerificator googleTokenVerificator;

    public JWTService(@Autowired PermissionChecker permissionChecker, @Autowired Environment environment, @Autowired GoogleTokenVerificator googleTokenVerificator) {
        this.permissionChecker = permissionChecker;
        this.environment = environment;
    }

    public String createJwt(User user, ClientDetails clientDetails) {
        return TokenCreator.createJWT(user, clientDetails, environment.getProperty("server.issuer"), environment.getProperty("server.audience"));
    }

    public ActionResult<Boolean> checkPermission(String header) {
        return permissionChecker.checkPermissions(header);
    }

    public Optional<String> extractClientIdFromHeader(String header) {
        return ClientIdExtractor.extractFromHeader(header);
    }

    public boolean hasUserInTokenHeaderRequiredRole(WarehouseRole requiredRole, String header) {
        return RoleChecker.hasRole(requiredRole, header);
    }

    public ActionResult<GoogleIdToken.Payload> getPayloadFromGoogleToken(String googleToken) {
        return googleTokenVerificator.verifyGoogleToken(googleToken);
    }
}
