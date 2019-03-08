package com.github.vedenin.luxhistory.webapplication.service;

import com.github.vedenin.luxhistory.model.Article;
import com.github.vedenin.luxhistory.webapplication.persistance.DataPopulationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataPopulationService {
    @Autowired
    private DataPopulationRepository repository;

    public void insertData(Article article) {
        repository.insert(article);
    }

}
