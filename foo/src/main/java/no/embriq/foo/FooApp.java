package no.embriq.foo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "no.embriq.foo")
public class FooApp {

    public static void main(String[] args) {
        SpringApplication.run(FooApp.class, args);
    }
}
