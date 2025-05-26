package com.example.food_delivery.controllers;

import com.example.food_delivery.models.Food;
import com.example.food_delivery.models.Order;
import com.example.food_delivery.models.User;
import com.example.food_delivery.services.FoodService;
import com.example.food_delivery.services.OrderService;
import com.example.food_delivery.services.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
public class AdminController {

	private final FoodService foodService;
	private final OrderService orderService;
	private final UserService userService;

	public AdminController(FoodService foodService, OrderService orderService, UserService userService) {
	    this.foodService = foodService;
	    this.orderService = orderService;
	    this.userService = userService;
	}


    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/foods")
    public String showFoodManagementPage(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Food> foodPage;

        if (search != null && !search.isEmpty()) {
            foodPage = foodService.searchFoodByNameForAdmin(search, pageable);
        } else {
            foodPage = foodService.getAllFoodForAdmin(pageable);
        }

        model.addAttribute("foods", foodPage.getContent());
        model.addAttribute("currentPage", foodPage.getNumber());
        model.addAttribute("totalPages", foodPage.getTotalPages());
        model.addAttribute("search", search);

        return "admin/food-list";
    }

    
    @GetMapping("/admin/foods/add")
    public String showAddFoodForm(Model model) {
        model.addAttribute("food", new Food()); // Empty food object for the form
        return "admin/food-form";
    }

    @PostMapping("/admin/foods/add")
    public String addFood(@ModelAttribute("food") Food food) {
        foodService.addFood(food);
        return "redirect:/admin/foods"; // Redirect back to food list after saving
    }
    
 // ✅ Show edit form with existing food data
    @GetMapping("/admin/foods/edit/{id}")
    public String showEditFoodForm(@PathVariable Long id, Model model) {
        Food food = foodService.getFoodById(id); // throws 404 if not found
        model.addAttribute("food", food);
        return "admin/food-form";
    }

    // ✅ Handle form submission and update the food
    @PostMapping("/admin/foods/edit/{id}")
    public String updateFood(@PathVariable Long id, @ModelAttribute("food") Food updatedFood) {
        foodService.updateFood(id, updatedFood);
        return "redirect:/admin/foods";
    }
    
    @GetMapping("/admin/foods/delete/{id}")
    public String deleteFood(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            foodService.deleteFood(id);
            redirectAttributes.addFlashAttribute("success", "Food item deleted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/foods";
    }

    @GetMapping("/admin/foods/toggle/{id}")
    public String toggleFoodStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Food food = foodService.getFoodById(id);
            food.setActive(!food.isActive());
            foodService.addFood(food); // Reuse save method
            redirectAttributes.addFlashAttribute("success", "Food visibility updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not update food status.");
        }
        return "redirect:/admin/foods";
    }
    
    @GetMapping("/admin/orders")
    public String viewAllOrders(Model model) {
        List<Order> allOrders = orderService.getAllOrders(); // We'll add this method in OrderService
        model.addAttribute("orders", allOrders);
        return "admin/order-list"; // This will be the Thymeleaf template
    }

    @GetMapping("/admin/users")
    public String viewUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<User> userPage;

        if (keyword != null && !keyword.isEmpty()) {
            userPage = userService.searchUsers(keyword, PageRequest.of(page, size));
        } else {
            userPage = userService.getAllUsers(PageRequest.of(page, size));
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "admin/user-list";
    }
   


    

}
