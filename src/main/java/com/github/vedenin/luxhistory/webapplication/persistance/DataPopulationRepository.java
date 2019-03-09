package com.github.vedenin.luxhistory.webapplication.persistance;

import com.github.vedenin.luxhistory.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataPopulationRepository extends MongoRepository<Article, String> {
}
