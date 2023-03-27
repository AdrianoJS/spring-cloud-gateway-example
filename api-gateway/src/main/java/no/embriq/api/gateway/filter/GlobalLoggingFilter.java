package no.embriq.api.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        var path = exchange.getRequest().getURI().getPath();
        var queryParameters = exchange.getRequest().getURI().getQuery();
        LOG.info("Received request at '{}'", path);
        if(queryParameters != null && !queryParameters.isBlank()) {
            LOG.debug("Query parameters were: {}", queryParameters);
        }
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> LOG.info("Done processing request for endpoint '{}'", path)));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
