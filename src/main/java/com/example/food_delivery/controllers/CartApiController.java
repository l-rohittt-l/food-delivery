package com.example.food_delivery.controllers;

import com.example.food_delivery.models.Cart;
import com.example.food_delivery.services.CartService;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getCartItems(HttpSession session) {
        String sessionId = getSessionId(session);
        List<Cart> cartItems = cartService.getCartItems(sessionId);
        return ResponseEntity.ok(cartItems);
    }


    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long foodId, @RequestParam int quantity, HttpSession session) {
        try {
            String sessionId = getSessionId(session);
            cartService.addToCart(foodId, quantity, sessionId);
            List<Cart> updatedCart = cartService.getCartItems(sessionId);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCartQuantity(@RequestParam Long foodId, @RequestParam int quantity, HttpSession session) {
        String sessionId = getSessionId(session);
        cartService.updateCartItemQuantity(foodId, sessionId, quantity);
        return ResponseEntity.ok(Map.of("message", "Quantity updated"));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItemFromCart(@RequestParam Long foodId, HttpSession session) {
        String sessionId = getSessionId(session);
        cartService.removeItemFromCart(foodId, sessionId);
        return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
    }

    @DeleteMapping("/clear")
    public void clearCart(HttpSession session) {
        String sessionId = getSessionId(session);
        cartService.clearCart(sessionId);
    }

    private String getSessionId(HttpSession session) {
        if (session.getAttribute("sessionId") == null) {
            session.setAttribute("sessionId", session.getId());
        }
        return (String) session.getAttribute("sessionId");
    }
}
