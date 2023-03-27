package no.embriq.api.gateway.filter;

import no.embriq.api.gateway.GatewayApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.ClearType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@MockServerTest
@AutoConfigureWebTestClient
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {GatewayApplication.class},
        properties = {
                "app.dependencies.foo.baseUrl=http://localhost:${mockServerPort}",
                "app.dependencies.bar.baseUrl=http://localhost:${mockServerPort}",
                "logging.level.org.mockserver.log.MockServerEventLog=warn"
        }
)
@ExtendWith(OutputCaptureExtension.class)
class GlobalLoggingFilterTest {

    @Autowired
    private WebTestClient client;

    private MockServerClient mockServer;

    @BeforeEach
    void beforeEach() {
        mockServer.clear(request(), ClearType.LOG);
    }

    @Test
    void testPreAndPostLogs(final CapturedOutput capturedOutput) {
        var logLocationBeforeTest = capturedOutput.getOut().length();
        mockServer.when(request("/api/add")).respond(response().withStatusCode(200));

        client.get().uri("/api/add").exchange();

        assertThat(capturedOutput.getOut().substring(logLocationBeforeTest))
                .contains("Received request at '/api/add'")
                .contains("Done processing request for endpoint '/api/add'")
                .doesNotContain("Query parameters were:");
    }

    @Test
    void testQueryParamLogs(final CapturedOutput capturedOutput) {
        var logLocationBeforeTest = capturedOutput.getOut().length();
        mockServer.when(request("/api/add")).respond(response().withStatusCode(200));

        client.get().uri("/api/add?a=1&b=2").exchange();

        assertThat(capturedOutput.getOut().substring(logLocationBeforeTest))
                .contains("Query parameters were: a=1&b=2");
    }
}