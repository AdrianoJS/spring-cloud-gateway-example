package no.embriq.api.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class AddAndSubtractCompositionFilterTest extends AbstractRunningApiGatewayTest {
    private static final Logger LOG = LoggerFactory.getLogger(AddAndSubtractCompositionFilterTest.class);
    @BeforeEach
    public void beforeEach() {
        clearMockServer();
    }

    @Test
    void testComposition() {
        mockServer.when(request("/api/add")
                        .withQueryStringParameter("a", "2")
                        .withQueryStringParameter("b", "3"))
                .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_JSON).withBody("{\"result\": 5}"));
        mockServer.when(request("/api/subtract")
                        .withQueryStringParameter("a", "5")
                        .withQueryStringParameter("b", "1"))
                .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_JSON).withBody("{\"result\": 4}"));

        client.get().uri("/addThenSubtract?a=2&b=3&c=1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("result").isEqualTo(4);
    }
}