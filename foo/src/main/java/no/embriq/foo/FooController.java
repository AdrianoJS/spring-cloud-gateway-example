package no.embriq.foo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(
        path = "/api",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class FooController {

    @GetMapping("/ping")
    public Mono<PingResponse> ping() {
        return Mono.just(new PingResponse(Status.UP, "foo"));
    }

    @GetMapping("/add")
    public Mono<AdditionResponse> add(@RequestParam int a, @RequestParam int b) {
        return Mono.just(new AdditionResponse(a + b));
    }

    enum Status {
        UP, DOWN
    }

    record AdditionResponse(int result) {
    }

    record PingResponse(Status status, String applicationName) {
    }
}
