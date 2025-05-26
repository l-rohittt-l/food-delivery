package com.example.food_delivery.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.food_delivery.models.User;
import com.example.food_delivery.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

@Controller
public class UserDashboardController {

	@Autowired
    private UserService userService;
	
    @GetMapping("/user/dashboard")
    public String showDashboard() {
        return "user/dashboard";  
    }
   
    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/profile/edit")
        public String showEditProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        model.addAttribute("user", user);
        return "user/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User existingUser = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (existingUser == null) {
            // If user not found, add error message to the page
            model.addAttribute("error", "User not found");
            return "user/edit-profile";
        }
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setAddress(updatedUser.getAddress());
            userService.save(existingUser);
        }
        return "redirect:/profile";
    }
    
}