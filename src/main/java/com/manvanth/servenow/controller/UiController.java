package com.manvanth.servenow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping({"/ui", "/ui/"})
    public String index() {
        return "index";
    }
}



