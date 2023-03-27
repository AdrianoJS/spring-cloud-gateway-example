package no.embriq.api.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class PingFilter extends AbstractGatewayFilterFactory<Object> {

    private static final String DEFAULT_RESPONSE = """
            {
                "status": "UP",
                "applicationName": "api-gateway"
            }
            """;

    @Override
    public GatewayFilter apply(final Object ignored) {
        return ((exchange, chain) -> {
            var response = exchange.getResponse();
            var buffer = response.bufferFactory().wrap(DEFAULT_RESPONSE.getBytes(UTF_8));
            response.setStatusCode(HttpStatusCode.valueOf(200));
            return response.writeWith(Flux.just(buffer));
        });
    }

    @Override
    public String name() {
        return "PingFilter";
    }
}
