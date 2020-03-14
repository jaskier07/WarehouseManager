package pl.kania.warehousemanager.services.security;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ClientIdExtractorTest {

    @DisplayName("Extract clientId claim from authorization header")
    @ParameterizedTest(name = "Extract claim \"{1}\" from header: {0}")
    @MethodSource(value = "pl.kania.warehousemanager.services.TestHeaderFactory#getValidHeadersWithClientId")
    void givenHeaderWithTokenContainingClaimsFindClientId(String header, String clientId) {
        Optional<String> found = ClientIdExtractor.extractFromHeader(header);
        assertNotEquals(Optional.empty(), found);

        Assumptions.assumeTrue(found.isPresent());
        assertEquals(clientId, found.get());
    }

    @DisplayName("Return Optional.empty() while trying to find claim clientId")
    @ParameterizedTest(name = "Try to extract claim from header: {0}")
    @MethodSource(value = "pl.kania.warehousemanager.services.TestHeaderFactory#getHeadersWithoutClaimClientId")
    void givenHeaderWithTokenNotContainingClaimClientIdReturnEmptyOptionalTryingToFindIt(String header) {
        Optional<String> found = ClientIdExtractor.extractFromHeader(header);
        assertEquals(Optional.empty(), found);
    }
}