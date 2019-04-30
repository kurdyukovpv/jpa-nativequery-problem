package ru.problem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.problem.service.TstAService;

import java.time.Duration;
import java.time.Instant;

import static java.lang.Thread.currentThread;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


@RestController
public class Controller {

    @Autowired
    private TstAService aService;

    @GetMapping(value = "/tst", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ControllerResponse> tst1(
            @RequestParam("sleep") @Nullable Long sleep
    ) {
        Instant start = Instant.now();
        aService.save(sleep);
        return ResponseEntity.ok(ControllerResponse.builder()
                .result("OK")
                .thread(currentThread().getName())
                .duration(Duration.between(start, Instant.now()))
                .build());
    }
}
