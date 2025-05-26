package com.example.food_delivery.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutUsController {

    @GetMapping("/about")
    public String aboutPage() {
        return "about"; // This will map to about.html in the templates folder
    }
}
