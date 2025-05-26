package com.example.food_delivery.repositories;

import com.example.food_delivery.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.food_delivery.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findBySessionId(String sessionId);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user = :user ORDER BY o.placedAt DESC")
    List<Order> findByUser(@Param("user") User user);
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.food ORDER BY o.placedAt DESC")
    List<Order> findAllWithItems();

    Page<Order> findByCustomerNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Order> findAllByOrderByPlacedAtDesc(Pageable pageable);
    
}
