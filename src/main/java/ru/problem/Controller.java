package ru.problem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.problem.service.TstAService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


@RestController
public class Controller {

    @Autowired
    private TstAService aService;

    @GetMapping(value = "/tst", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity tst1() {
        aService.save();
        return ResponseEntity.ok("{\"result\":\"OK\"}");
    }
}
