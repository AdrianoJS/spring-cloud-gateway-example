package no.embriq.bar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "no.embriq.bar")
public class BarApp {

    public static void main(String[] args) {
        SpringApplication.run(BarApp.class, args);
    }
}
