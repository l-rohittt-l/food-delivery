package com.example.food_delivery.controllers;

import com.example.food_delivery.models.Order;
import com.example.food_delivery.models.User;
import com.example.food_delivery.services.OrderService;
import com.example.food_delivery.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class UserOrderController {

    private final UserService userService;
    private final OrderService orderService;

    public UserOrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/user/orders")
    public String viewMyOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            return "redirect:/login?error";
        }

        if (optionalUser.isPresent()) {
            List<Order> orders = orderService.getOrdersByUser(optionalUser.get());
            model.addAttribute("orders", orders);
        } else {
            model.addAttribute("orders", List.of());
        }
        return "user/my-orders";
    }

}
