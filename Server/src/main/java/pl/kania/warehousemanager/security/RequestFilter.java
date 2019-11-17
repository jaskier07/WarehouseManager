package pl.kania.warehousemanager.security;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

    private static final int UNAUTHORIZED_CODE = 401;
    private static final int SUCCESS_CODE = 200;

    @Autowired
    private Environment environment;

    @Autowired
    private JWTService jwtService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // TODO tylko https
        final RequestFacade requestFacade = (RequestFacade) request;
        final ResponseFacade responseFacade = (ResponseFacade) response;
        if (resourceAddressStartsWith("/h2-console", requestFacade)) {
            chain.doFilter(request, response);
        } else if (!hasAuthorizationHeader(requestFacade)) {
            responseFacade.setStatus(UNAUTHORIZED_CODE, "Lack of authentication header. Please log in.");
            responseFacade.encodeRedirectURL(environment.getProperty("server.url") + "/login");
        } else if (resourceAddressStartsWith("/login", requestFacade)) {
            chain.doFilter(request, response);
        } else {
            Consumer<String> responseStatusSetter = text -> responseFacade.setStatus(UNAUTHORIZED_CODE, text);
            if (jwtService.checkPermissions(requestFacade.getHeader("Authorization"), responseStatusSetter)) {
                if (resourceAddressStartsWith("/check-token", requestFacade)) {
                    responseFacade.setStatus(SUCCESS_CODE);
                } else {
                    chain.doFilter(request, response);
                }
            }
        }
    }

    private boolean resourceAddressStartsWith(String address, RequestFacade requestFacade) {
        return requestFacade.getRequestURI().startsWith(address);
    }

    private boolean hasAuthorizationHeader(RequestFacade requestFacade) {
        String header = requestFacade.getHeader("Authorization");
        return header != null;
    }
}
