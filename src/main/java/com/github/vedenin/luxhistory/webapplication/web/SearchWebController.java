package com.github.vedenin.luxhistory.webapplication.web;

import com.github.vedenin.luxhistory.webapplication.model.Search;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*
 * Controller that provide main page of Converter application
 */
@Controller
public class SearchWebController extends WebMvcConfigurerAdapter {
    @GetMapping("/")
    public String greetingForm(Model model) {
        Search search = new Search();
        search.setResult("");
        model.addAttribute("search", search);
        return "search";
    }

    @PostMapping("/")
    public String greetingSubmit(@ModelAttribute Search search) {
        search.setResult("result for query " + search.getQuery());
        return "search";
    }
}
