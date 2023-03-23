package no.embriq.foo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(FooController.class)
class FooControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void testPing() {
        client.get().uri("/api/ping")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("status").isEqualTo("UP")
                .jsonPath("applicationName").isEqualTo("foo");
    }

    @Test
    void testAdd() {
        client.get().uri("/api/add?a=1&b=2")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("result").isEqualTo(3);
    }
}