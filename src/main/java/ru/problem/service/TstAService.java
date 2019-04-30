package ru.problem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.problem.dao.TstARepository;
import ru.problem.model.TstA;

import javax.transaction.Transactional;
import java.time.LocalDateTime;


@Slf4j
@Service
public class TstAService {

    @Autowired
    private TstARepository repository;

    @Autowired
    private TstBService bService;

    @Autowired
    private TstCService cService;

    @Transactional
    public void save(Long sleep) {
        TstA tstA = TstA.builder().name(LocalDateTime.now().toString()).build();

        bService.updateTstsB(); //update TstB (findById+save) @Transactional
        cService.executeNative(); // native query 'select 1' @Transactional
        repository.save(tstA); // insert TstA @Transactional

        //emulate long slow transaction if requested
        try {
            if (sleep != null) {
                Thread.sleep(sleep);
            }
        } catch (InterruptedException e) {
            log.error("Exception", e);
        }
    }
}
