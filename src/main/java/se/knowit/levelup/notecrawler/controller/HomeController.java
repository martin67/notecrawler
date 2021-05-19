package se.knowit.levelup.notecrawler.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.knowit.levelup.notecrawler.BasicCrawlController;

@Controller
public class HomeController {
    final Logger log = LoggerFactory.getLogger(HomeController.class);

    final BasicCrawlController basicCrawlController;

    public HomeController(BasicCrawlController basicCrawlController) {
        this.basicCrawlController = basicCrawlController;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        log.debug("Hopp");
        return "index";
    }

    @PostMapping(value = "/", params = "start")
    public String start(@ModelAttribute("searchForm") SearchForm searchForm) {
        log.info("Starting crawler: {}", searchForm.getText());
        basicCrawlController.start();
        return "index";
    }

    @PostMapping(value = "/", params = "stop")
    public String stop(@ModelAttribute("searchForm") SearchForm searchForm) {
        log.info("Stopping crawler");
        basicCrawlController.shutdown();
        return "index";
    }
}
