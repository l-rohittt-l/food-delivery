package com.example.food_delivery.controllers;

import com.example.food_delivery.models.Food;
import com.example.food_delivery.services.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    private final FoodService foodService;

    @Autowired
    public HomeController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<Food> foodList = foodService.getAllFood();
        model.addAttribute("foodList", foodList);
        return "index";
    }
}
