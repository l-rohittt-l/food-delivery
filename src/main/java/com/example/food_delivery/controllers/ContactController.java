package com.example.food_delivery.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {

    @GetMapping("/contact")
    public String contactPage() {
        return "contact"; // loads templates/contact.html
    }
}
