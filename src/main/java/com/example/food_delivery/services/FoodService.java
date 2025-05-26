package com.example.food_delivery.services;

import com.example.food_delivery.models.Food;
import com.example.food_delivery.repositories.FoodRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@Service
public class FoodService {

    private final FoodRepository foodRepository;

    @Autowired
    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    // Add a new food item
    public Food addFood(@Valid Food food) {
        return foodRepository.save(food);
    }

    // Get all food items
    public List<Food> getAllFood() {
        return foodRepository.findAllByActiveTrue();
    }
    
    public List<Food> getAllFoodForAdmin() {
        return foodRepository.findAll();
    }


    // Get a food item by ID
    public Food getFoodById(Long id) {
        return foodRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food item not found with ID: " + id));
    }


    // Get food items by category
    public List<Food> getFoodByCategory(String category) {
        return foodRepository.findByCategory(category);
    }

    // Get food items within a price range
    public List<Food> getFoodByPriceRange(double minPrice, double maxPrice) {
        return foodRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // Update a food item
    public Food updateFood(Long id, @Valid Food foodDetails) {
        return foodRepository.findById(id).map(food -> {
            food.setName(foodDetails.getName());
            food.setCategory(foodDetails.getCategory());
            food.setDescription(foodDetails.getDescription());
            food.setPrice(foodDetails.getPrice());
            food.setImageUrl(foodDetails.getImageUrl());
            return foodRepository.save(food);
        }).orElseThrow(() -> new RuntimeException("Food item not found"));
    }

    // Delete a food item
    public void deleteFood(Long id) {
        try {
            if (!foodRepository.existsById(id)) {
                throw new RuntimeException("Food item not found");
            }
            foodRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete food item because it is associated with existing orders.");
        }
    }

    
    public List<Food> searchFoodByName(String keyword) {
        return foodRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Page<Food> getPaginatedFood(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }
    
    public Page<Food> searchFoodByNameForAdmin(String keyword, Pageable pageable) {
        return foodRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    
    public Page<Food> getAllFoodForAdmin(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }
}
