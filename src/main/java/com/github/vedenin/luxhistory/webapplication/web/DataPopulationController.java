package com.github.vedenin.luxhistory.webapplication.web;

import com.github.vedenin.luxhistory.model.Article;
import com.github.vedenin.luxhistory.webapplication.service.DataPopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/datapopulation")
public class DataPopulationController {

    @Autowired
    private DataPopulationService dataPopulationService;

    @PutMapping("/article")
    public @ResponseBody
    ResponseEntity getListOfProviders(@RequestBody Article article) {
        dataPopulationService.insertData(article);
        return new ResponseEntity<>("Response from PUT method", HttpStatus.OK);
    }
}
