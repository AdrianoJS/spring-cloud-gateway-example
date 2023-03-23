package no.embriq.bar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(BarController.class)
class BarControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void testPing() {
        client.get().uri("/api/ping")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("status").isEqualTo("UP")
                .jsonPath("applicationName").isEqualTo("bar");
    }

    @Test
    void testSubtract() {
        client.get().uri("/api/subtract?a=1&b=2")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("result").isEqualTo(-1);
    }
}