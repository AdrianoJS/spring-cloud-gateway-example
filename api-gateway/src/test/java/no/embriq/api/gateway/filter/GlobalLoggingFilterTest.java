package no.embriq.api.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


@ExtendWith(OutputCaptureExtension.class)
class GlobalLoggingFilterTest extends AbstractRunningApiGatewayTest {

    @BeforeEach
    void beforeEach() {
        clearMockServer();
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