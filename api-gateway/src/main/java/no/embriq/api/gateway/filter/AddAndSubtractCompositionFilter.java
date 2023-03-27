package no.embriq.api.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class AddAndSubtractCompositionFilter extends AbstractGatewayFilterFactory<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(AddAndSubtractCompositionFilter.class);

    @Value("${app.dependencies.foo.baseUrl}/api/add?")
    private String fooUrl;

    @Value("${app.dependencies.bar.baseUrl}/api/subtract?")
    private String barUrl;

    @Override
    public GatewayFilter apply(final Object ignored) {
        return ((exchange, chain) -> {
            var response = exchange.getResponse();

            var compositionResult = executeComposition(exchange.getRequest())
                    .map(s -> convertToDataBuffer(response, s))
                    .timeout(Duration.ofSeconds(5))
                    .doOnSuccess(dataBuffer -> {
                        printDataBufferContent(dataBuffer);
                        response.setStatusCode(HttpStatusCode.valueOf(200));
                        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    });

            return response.writeWith(compositionResult);
        });
    }

    @Override
    public String name() {
        return "CompositionFilter";
    }

    private DataBuffer convertToDataBuffer(final ServerHttpResponse response, final String s) {
        return response.bufferFactory().wrap(s.getBytes(UTF_8));
    }

    /**
     * Dangerous!
     * <p>
     * Could run out of memory if large object as entire buffer is read into memory.
     * Only here for educational purposes.
     *
     * @param dataBuffer
     */
    private void printDataBufferContent(final DataBuffer dataBuffer) {
        var bufferAsBytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bufferAsBytes);
        var bufferAsString = new String(bufferAsBytes);
        LOG.info("Composition result was '{}'", bufferAsString);

        // Necessary as we just read (and emptied) the buffer.
        // No result would be returned to the caller otherwise!
        // This is also a technique to rewrite the response
        // Instead of reading the buffer, you could execute dataBuffer.readPosition(dataBuffer.writePosition())
        // Useful in combination with ServerHttpResponseDecorator
        // If a different response was to be sent, you would have to set the content-length header to the new value.
        // e.g. exchange.getResponse().getHeaders().setContentLength(int)
        // If content-length is not set, you risk either cutting the response short or hanging indefinitely waiting for the remainder of the response.
        dataBuffer.write(bufferAsBytes);
    }

    /**
     * All input SHOULD be validated and content should be scanned for security threats,
     * e.g. XSS, content bombs (zip, xml etc.), size (DOS by using up available memory) etc.
     * <p>
     * Skipped for simplicity
     *
     * @param request
     * @return
     */
    private Mono<String> executeComposition(final ServerHttpRequest request) {
        var client = WebClient.builder().build();
        var queryParams = request.getQueryParams();
        var additionQueryParams = "a=" + queryParams.get("a").get(0) + "&b=" + queryParams.get("b").get(0);
        var subtractionQueryParams = "a=%s&b=" + queryParams.get("c").get(0);

        return client.get()
                .uri(fooUrl + additionQueryParams) // Should validate query is non-threatening
                .retrieve()
                .onStatus(HttpStatusCode::isError, ClientResponse::createError)
                .bodyToMono(JsonNode.class)
                .flatMap(additionResponse -> client.get()
                        .uri(barUrl + subtractionQueryParams.formatted(additionResponse.get("result").asText()))
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, ClientResponse::createError)
                        .bodyToMono(String.class)
                );
    }
}
