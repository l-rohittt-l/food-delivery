package com.example.food_delivery.repositories;

import com.example.food_delivery.models.Food;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    // Find all food items by category (e.g., Vegetarian, Non-Vegetarian)
    List<Food> findByCategory(String category);

    // Find all food items within a price range
    List<Food> findByPriceBetween(double minPrice, double maxPrice);
    
    
    List<Food> findByNameContainingIgnoreCase(String keyword);
    
    List<Food> findAllByActiveTrue();
    
    Page<Food> findByNameContainingIgnoreCase(String name, Pageable pageable);

}