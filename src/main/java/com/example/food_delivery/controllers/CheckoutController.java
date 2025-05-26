package com.example.food_delivery.controllers;

import com.example.food_delivery.models.Cart;
import com.example.food_delivery.models.Order;
import com.example.food_delivery.models.User;
import com.example.food_delivery.services.CartService;
import com.example.food_delivery.services.OrderService;
import com.example.food_delivery.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    public CheckoutController(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    // ✅ Render the checkout page
    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        String sessionId = getSessionId(session);
        List<Cart> cartItems = cartService.getCartItems(sessionId);

        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getFood().getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);

        return "checkout";
    }

    // ✅ Handle order form submission
    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String address,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpSession session,
                             Model model) {

        String sessionId = getSessionId(session);
        List<Cart> cartItems = cartService.getCartItems(sessionId);

        if (cartItems.isEmpty()) {
            model.addAttribute("error", "Your cart is empty!");
            return "checkout";
        }

        // ✅ Check if user is logged in
        if (userDetails == null) {
            session.setAttribute("redirectAfterLogin", "/checkout");
            return "redirect:/login";
        }

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "User not found!");
            return "checkout";
        }

        User user = optionalUser.get();

        Order order = orderService.createOrder(user, name, email, address, sessionId, cartItems);
        cartService.clearCart(sessionId);
        model.addAttribute("order", order);

        return "order-confirmation";
    }



    private String getSessionId(HttpSession session) {
        if (session.getAttribute("sessionId") == null) {
            session.setAttribute("sessionId", session.getId());
        }
        return (String) session.getAttribute("sessionId");
    }
}
