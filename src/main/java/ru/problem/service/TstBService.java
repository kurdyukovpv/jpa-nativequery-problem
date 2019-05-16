package ru.problem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.problem.dao.TstBRepository;
import ru.problem.model.TstB;


@Service
@Slf4j
public class TstBService {

    private final TstBRepository repository;

    @Autowired
    public TstBService(TstBRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void updateTstsB(long id, boolean flush) {
        TstB tstB = repository.findById(id).get();
        Long value = tstB.getValue();
        log.info("Value = {}", value);
        tstB.setValue(++value);
        repository.save(tstB);
        if (flush) {
            repository.flush();
        }
    }
}

