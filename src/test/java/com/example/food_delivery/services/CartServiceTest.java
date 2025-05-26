package com.example.food_delivery.services;

import com.example.food_delivery.models.Cart;
import com.example.food_delivery.models.Food;
import com.example.food_delivery.repositories.CartRepository;
import com.example.food_delivery.repositories.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private CartService cartService;

    private Food mockFood;
    private Cart mockCart;
    private final String sessionId = "test-session";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // ✅ Initialize mockFood properly
        mockFood = new Food();
        mockFood.setId(1L);
        mockFood.setName("Pizza");
        mockFood.setPrice(10.99);
        mockFood.setCategory("Vegetarian");
        mockFood.setDescription("Cheese pizza");
        mockFood.setImageUrl("https://example.com/pizza.jpg");

        // ✅ Initialize mockCart
        mockCart = new Cart(mockFood, 1, sessionId);
        mockCart.setId(1L);
    }

    @Test
    void testAddToCart_NewItem() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(mockFood));
        when(cartRepository.findByFoodIdAndSessionId(1L, sessionId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        Cart result = cartService.addToCart(1L, 2, sessionId);

        assertNotNull(result);
        assertEquals(mockFood.getId(), result.getFood().getId());
        assertEquals(2, result.getQuantity());
        assertEquals(sessionId, result.getSessionId());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddToCart_UpdateQuantity() {
        Cart existingCart = new Cart(mockFood, 1, sessionId);
        existingCart.setId(1L);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(mockFood));
        when(cartRepository.findByFoodIdAndSessionId(1L, sessionId)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(existingCart);

        Cart result = cartService.addToCart(1L, 3, sessionId);

        assertNotNull(result);
        assertEquals(4, result.getQuantity());
    }

    @Test
    void testAddToCart_InvalidFoodId() {
        when(foodRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(99L, 1, sessionId);
        });

        assertEquals("Food item not found", exception.getMessage());
    }

    @Test
    void testClearCart() {
        doNothing().when(cartRepository).deleteBySessionId(sessionId);

        cartService.clearCart(sessionId);

        verify(cartRepository, times(1)).deleteBySessionId(sessionId);
    }

    @Test
    void testRemoveItemFromCart() {
        doNothing().when(cartRepository).deleteByFoodIdAndSessionId(1L, sessionId);

        cartService.removeItemFromCart(1L, sessionId);

        verify(cartRepository, times(1)).deleteByFoodIdAndSessionId(1L, sessionId);
    }
}
