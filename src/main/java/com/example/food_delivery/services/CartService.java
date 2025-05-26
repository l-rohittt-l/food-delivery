package com.example.food_delivery.services;

import com.example.food_delivery.models.Cart;
import com.example.food_delivery.models.Food;
import com.example.food_delivery.repositories.CartRepository;
import com.example.food_delivery.repositories.FoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final FoodRepository foodRepository;

    public CartService(CartRepository cartRepository, FoodRepository foodRepository) {
        this.cartRepository = cartRepository;
        this.foodRepository = foodRepository;
    }

    public List<Cart> getCartItems(String sessionId) {
        return cartRepository.findBySessionId(sessionId);
    }

    public Cart addToCart(Long foodId, int quantity, String sessionId) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Optional<Food> food = foodRepository.findById(foodId);
        if (food.isEmpty()) {
            throw new RuntimeException("Food item not found");
        }

        Optional<Cart> existingCartItem = cartRepository.findByFoodIdAndSessionId(foodId, sessionId);

        if (existingCartItem.isPresent()) {
            // Update quantity if item exists
            Cart cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartRepository.save(cartItem);
        } else {
            // Create new cart item if it does not exist
            Cart newCartItem = new Cart(food.get(), quantity, sessionId);
            return cartRepository.save(newCartItem);
        }
    }


    @Transactional 
    public void clearCart(String sessionId) {
        cartRepository.deleteBySessionId(sessionId);
        
        
    }
    
    @Transactional
    public void removeItemFromCart(Long foodId, String sessionId) {
        cartRepository.deleteByFoodIdAndSessionId(foodId, sessionId);
    }

    
    @Transactional
    public void updateCartItemQuantity(Long foodId, String sessionId, int quantity) {
        Optional<Cart> cartItem = cartRepository.findByFoodIdAndSessionId(foodId, sessionId);
        cartItem.ifPresent(item -> {
            item.setQuantity(quantity);
            cartRepository.save(item);
        });
    }


}
