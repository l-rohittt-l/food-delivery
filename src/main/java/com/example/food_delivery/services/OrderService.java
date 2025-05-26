package com.example.food_delivery.services;

import com.example.food_delivery.models.Cart;
import com.example.food_delivery.models.Order;
import com.example.food_delivery.models.OrderItem;
import com.example.food_delivery.models.User;
import com.example.food_delivery.repositories.CartRepository;
import com.example.food_delivery.repositories.OrderRepository;
import com.example.food_delivery.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order placeOrder(User user, String sessionId) {
        List<Cart> cartItems = cartRepository.findBySessionId(sessionId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setPlacedAt(LocalDateTime.now());
        order.setCustomerName(user.getName());
        order.setCustomerEmail(user.getEmail());
        order.setCustomerAddress(user.getAddress());
        order.setSessionId(sessionId); // for compatibility

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getFood().getPrice() * item.getQuantity())
                .sum();

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        cartRepository.deleteBySessionId(sessionId);

        return savedOrder;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    // 🔄 Updated to accept User and assign it to Order
    public Order createOrder(User user, String name, String email, String address, String sessionId, List<Cart> cartItems) {
        Order order = new Order();
        order.setUser(user); // ✅ assign user
        order.setSessionId(sessionId);
        order.setPlacedAt(LocalDateTime.now());
        order.setCustomerName(name);
        order.setCustomerEmail(email);
        order.setCustomerAddress(address);

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getFood().getPrice() * item.getQuantity())
                .sum();

        order.setTotalAmount(totalAmount);
        
        System.out.println("Placing order for user: " + user.getEmail() + " ID: " + user.getId());

        
        return orderRepository.save(order);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithItems();
    }
    
    
    


}
