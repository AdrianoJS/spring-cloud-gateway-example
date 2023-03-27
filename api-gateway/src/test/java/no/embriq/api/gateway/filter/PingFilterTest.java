package no.embriq.api.gateway.filter;

import org.junit.jupiter.api.Test;

public class PingFilterTest extends AbstractRunningApiGatewayTest {

    @Test
    void testPing() {
        client.get().uri("/ping")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("status").isEqualTo("UP")
                .jsonPath("applicationName").isEqualTo("api-gateway");
    }
}
