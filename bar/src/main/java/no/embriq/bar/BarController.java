package no.embriq.bar;

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
public class BarController {

    @GetMapping("/ping")
    public Mono<PingResponse> ping() {
        return Mono.just(new PingResponse(Status.UP, "bar"));
    }

    @GetMapping("/subtract")
    public Mono<SubtractionResponse> subtract(@RequestParam int a, @RequestParam int b) {
        return Mono.just(new SubtractionResponse(a - b));
    }

    enum Status {
        UP, DOWN
    }

    record SubtractionResponse(int result) {
    }

    record PingResponse(Status status, String applicationName) {
    }
}
