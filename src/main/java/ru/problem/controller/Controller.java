package ru.problem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import ru.problem.service.TstAService;

import java.time.Duration;
import java.time.Instant;

import static java.lang.Thread.currentThread;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


@RestController
@Slf4j
public class Controller {

    @Autowired
    private TstAService aService;

    @GetMapping(value = "/tst", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ControllerResponse> tst1(
            @ModelAttribute ControllerRequest request
    ) {
        log.info("Request: {}", request);
        Instant start = Instant.now();
        aService.save(request);
        return ResponseEntity.ok(ControllerResponse.builder()
                .result("OK")
                .thread(currentThread().getName())
                .duration(Duration.between(start, Instant.now()))
                .build());
    }
}
