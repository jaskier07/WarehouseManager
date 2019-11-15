package pl.kania.warehousemanager.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Slf4j
@Component
@Order(0)
public class RequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestFacade requestFacade = (RequestFacade) request;
        ResponseFacade responseFacade = (ResponseFacade) response;
        if (requestFacade.getRequestURI().equals("/login")) {
            // dopiero chcę się zalogować - spoko
            chain.doFilter(request, response);
        }
        if (requestFacade.getRequestURI().equals("/oauth/authorization")) {
            // dopiero chcę się zalogować - spoko
            chain.doFilter(request, response);
        }
        if (requestFacade.getHeader("Authorization") == null) {
            // nie ma nagłówka autoryzacji, odmowa dostępu
            responseFacade.setStatus(401, "Brak nagłówka Authorization. Żądanie nie dotyczy logowania.");
        }
    }
}
