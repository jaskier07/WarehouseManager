package pl.kania.warehousemanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pl.kania.warehousemanager.dao.ClientDetailsRepository;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

@Component
@Order(0)
public class RequestFilter implements Filter {

    @Autowired
    private Environment environment;

    @Autowired
    private ClientDetailsRepository clientDetailsRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestFacade requestFacade = (RequestFacade) request;
        ResponseFacade responseFacade = (ResponseFacade) response;
        if (requestFacade.getRequestURI().equals("/login")) {
            chain.doFilter(request, response);
        } else if (requestFacade.getRequestURI().startsWith("/h2-console")) {
            chain.doFilter(request, response);
        } else if (!hasAuthorizationHeader(requestFacade)) {
            // nie ma nagłówka autoryzacji, odmowa dostępu
            responseFacade.setStatus(401, "Lack of authentication header. Please log in.");
            responseFacade.encodeRedirectURL(environment.getProperty("server.url") + "/login");
        } else {
            Consumer<String> responseStatusSetter = text -> responseFacade.setStatus(401, text);
            checkPermissions(requestFacade, responseStatusSetter);
        }
    }

    private boolean checkPermissions(RequestFacade requestFacade, Consumer<String> responseSetter) {
        final String header = requestFacade.getHeader("Authorization");
        final String token = header.substring(header.indexOf(" ") + 1).trim();
        final DecodedJWT decodedJwt = JWT.decode(token);

        final Claim clientIdClaim = decodedJwt.getClaim("clientId");
        if (clientIdClaim.isNull()) {
            responseSetter.accept("Lack of client id.");
            return false;
        }

        final String clientId = clientIdClaim.asString();
        final String clientSecret = clientDetailsRepository.findClientSecretByClientId(clientId);
        if (clientSecret == null) {
            responseSetter.accept("Bad client id.");
            return false;
        }

        final DecodedJWT validatedJwt = validateIntegrity(decodedJwt, clientSecret, responseSetter);
        if (validatedJwt == null) {
            return false;
        }

        if (!decodedJwt.getIssuer().equals(environment.getProperty("server.issuer"))) {
            responseSetter.accept("Invalid issuer");
        } else if (!decodedJwt.getAudience().contains(environment.getProperty("server.audience"))) {
            responseSetter.accept("Server is not in the audience.");
        } else {
            return true;
        }
        return false;
    }

    private DecodedJWT validateIntegrity(DecodedJWT token, String clientSecret, Consumer<String> responseSetter) {
        try {
            return JWT.require(Algorithm.HMAC256(clientSecret))
                    .withIssuer(environment.getProperty("server.issuer"))
                    .withAudience(environment.getProperty("server.audience"))
                    .build()
                    .verify(token);
        } catch (AlgorithmMismatchException ame) {
            responseSetter.accept("Wrong alghoritm.");
        } catch (SignatureVerificationException sve) {
            responseSetter.accept("Invalid signature.");
        } catch (TokenExpiredException tee) {
            responseSetter.accept("Token expired.");
        } catch (InvalidClaimException ice) {
            responseSetter.accept("Invalid claim.");
        }
        return null;
    }

    private boolean hasAuthorizationHeader(RequestFacade requestFacade) {
        String header = requestFacade.getHeader("Authorization");
        return header != null && header.toLowerCase().startsWith("bearer");
    }
}
