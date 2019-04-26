package ru.problem.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;


@Service
@Slf4j
public class TstCService {

    @Autowired
    private EntityManager em;

    @Transactional
    public void get() {
        Query q = em.createNativeQuery("select 1");
        q.getResultList();
    }
}
