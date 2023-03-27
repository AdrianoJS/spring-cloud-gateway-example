package no.embriq.api.gateway;

import no.embriq.api.gateway.filter.AddAndSubtractCompositionFilter;
import no.embriq.api.gateway.filter.PingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class Routes implements RouteLocator {

    private static final String NOOP = "https://mandatory.url.that.is.ignored.due.to.filter.logic";
    @Autowired
    private RouteLocatorBuilder builder;

    @Value("${app.dependencies.foo.baseUrl}")
    private String fooUrl;

    @Value("${app.dependencies.bar.baseUrl}")
    private String barUrl;

    @Autowired
    private PingFilter pingFilter;

    @Autowired
    private AddAndSubtractCompositionFilter addAndSubtractCompositionFilter;

    @Override
    public Flux<Route> getRoutes() {
        return builder.routes()
                .route("optional id",
                        r -> r.path("/api/add")
                                .uri(fooUrl))
                .route(r -> r.path("/api/subtract")
                        .uri(barUrl))
                .route(r -> r.path("/foo/ping")
                        .filters(f -> f.rewritePath("/foo/ping", "/api/ping"))
                        .uri(fooUrl))
                .route(r -> r.path("/bar/ping")
                        .filters(f -> f.rewritePath("/bar/ping", "/api/ping"))
                        .uri(barUrl))
                .route(r -> r.path("/ping")
                        .filters(f -> f.filter(pingFilter.apply(new Object()))) // pingFilter.apply parameter cannot be null even though it is ignored. Throws NPE
                        .uri(NOOP))
                .route(r -> r.path("/addThenSubtract")
                        .filters(f -> f.filter(addAndSubtractCompositionFilter.apply(new Object())))
                        .uri(NOOP))
                .build().getRoutes();

    }
}
