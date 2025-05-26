package com.example.food_delivery.controllers;

import com.example.food_delivery.models.User;
import com.example.food_delivery.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; 
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model) {

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        try {
            userService.registerUser(user);
            return "redirect:/login"; // after successful registration
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
