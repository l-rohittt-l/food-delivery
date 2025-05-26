package com.example.food_delivery.controllers;

import com.example.food_delivery.models.Cart;
import com.example.food_delivery.services.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CartPageController {

    private final CartService cartService;

    public CartPageController(CartService cartService) {
        this.cartService = cartService;
    }
    
    @GetMapping("/cart")
    public String viewCartPage(HttpSession session, Model model) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            sessionId = session.getId();
            session.setAttribute("sessionId", sessionId);
        }

        List<Cart> cartItems = cartService.getCartItems(sessionId);
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getFood().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "cart"; // ✅ loads cart.html
    }
}
