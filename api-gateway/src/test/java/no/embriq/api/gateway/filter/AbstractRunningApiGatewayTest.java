package no.embriq.api.gateway.filter;

import no.embriq.api.gateway.GatewayApplication;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.ClearType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockserver.model.HttpRequest.request;

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
public abstract class AbstractRunningApiGatewayTest {
    @Autowired
    protected WebTestClient client;

    protected MockServerClient mockServer;

    protected void clearMockServer() {
        mockServer.clear(request(), ClearType.LOG);
    }
}
