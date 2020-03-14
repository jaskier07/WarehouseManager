package pl.kania.warehousemanager.services.beans;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.kania.warehousemanager.exceptions.TokenNotFoundInHeaderException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeaderExtractorTest {

    private static final String TOKEN = "1c01c352-0bc3-43bf-b851-0d1a533cc79f";

    @ParameterizedTest(name = "Throw exception TokenNotFound trying to extract token from Authorization header: {0}")
    @ValueSource(strings = {"BEARER" + TOKEN, "BEARER." + TOKEN, TOKEN})
    void givenStringWithInvalidAuthorizationHeaderTryToExtractTokenThenThrowTokenNotFoundException(String header) {
        assertThrows(TokenNotFoundInHeaderException.class, () -> HeaderExtractor.extractTokenFromAuthorizationHeader(header));
    }

    @ParameterizedTest(name = "Extract token from authorization header: \"{0}\"")
    @MethodSource(value = "pl.kania.warehousemanager.services.TestHeaderFactory#getValidHeadersWithToken")
    void givenStringWithValidAuthorizationHeaderTryToExtractTokenThenReturnIt(String header, String tokenToExtract) throws TokenNotFoundInHeaderException {
        Optional<String> token = HeaderExtractor.extractTokenFromAuthorizationHeader(header);
        assertNotEquals(Optional.empty(), token);

        Assumptions.assumeTrue(token.isPresent());
        assertEquals(tokenToExtract, token.get());
    }
}