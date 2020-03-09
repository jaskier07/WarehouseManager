package pl.kania.warehousemanager.services.beans;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HeaderExtractorTest {

    private static final String TOKEN = "1c01c352-0bc3-43bf-b851-0d1a533cc79f";

    @Autowired
    private HeaderExtractor headerExtractor;

    @ParameterizedTest(name = "Throw exception TokenNotFound trying to extract token from Authorization header: {0}")
    @ValueSource(strings = {"BEARER" + TOKEN, "BEARER." + TOKEN, TOKEN})
    void givenStringWithInvalidAuthorizationHeaderTryToExtractTokenThenThrowTokenNotFoundException(String header) {
        assertThrows(TokenNotFoundInHeaderException.class, () -> headerExtractor.extractTokenFromAuthorizationHeader(header));
    }

    @ParameterizedTest(name = "Extract token from authorization header: \"{0}\"")
    @ValueSource(strings = {
            "BEARER " + TOKEN,
            "Bearer " + TOKEN,
            "bearer " + TOKEN,
            "BEARER " + TOKEN + " "
    })
    void givenStringWithValidAuthorizationHeaderTryToExtractTokenThenReturnIt(String header) throws TokenNotFoundInHeaderException {
        Optional<String> token = headerExtractor.extractTokenFromAuthorizationHeader(header);
        assertNotEquals(Optional.empty(), token);

        Assumptions.assumeFalse(!token.isPresent());
        assertEquals(TOKEN, token.get());
    }
}