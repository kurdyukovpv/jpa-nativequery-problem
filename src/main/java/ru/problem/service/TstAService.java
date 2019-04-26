package ru.problem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.problem.dao.TstARepository;
import ru.problem.model.TstA;
import ru.problem.model.TstB;

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

    private int counter = 0;

    @Transactional
    public void save1() {
        TstB tstB = new TstB();
        tstB.setId(1L);
        TstA tstA = TstA.builder().name(LocalDateTime.now().toString()).tstB(tstB).build();

        bService.save(); //делаем что-то с сущностью TstB

        cService.get(); //делаем что-то нативно

        repository.save(tstA); //сохраняем родительскую сущность

        //Эмулируем долгую транзакцию для каждого второго запроса
        counter++;
        log.info("counter {}", counter);
        try {
            if (counter % 2 == 0) {
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            log.error("Exception", e);
        }
    }
}
