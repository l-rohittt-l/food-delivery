package com.example.food_delivery.controllers;

import com.example.food_delivery.models.Food;
import com.example.food_delivery.services.FoodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    // 1️⃣ Add a new food item
    @PostMapping
    public ResponseEntity<Food> addFood(@Valid @RequestBody Food food) {
        Food savedFood = foodService.addFood(food);
        return ResponseEntity.ok(savedFood);
    }

    // 2️⃣ Get all food items
    @GetMapping("all")
    public ResponseEntity<List<Food>> getAllFood() {
        return ResponseEntity.ok(foodService.getAllFood());
    }

    // 3️⃣ Get a food item by ID
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {
        Food food = foodService.getFoodById(id);
        return ResponseEntity.ok(food); // 200 OK with the food item
    }


    // 4️⃣ Get food items by category
    @GetMapping("/category")
    public ResponseEntity<List<Food>> getFoodByCategory(@RequestParam String category) {
        return ResponseEntity.ok(foodService.getFoodByCategory(category));
    }


    // 5️⃣ Get food items within a price range
    @GetMapping("/price-range")
    public ResponseEntity<List<Food>> getFoodByPriceRange(@RequestParam double min, @RequestParam double max) {
        return ResponseEntity.ok(foodService.getFoodByPriceRange(min, max));
    }

    // 6️⃣ Update a food item
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(@PathVariable Long id, @Valid @RequestBody Food foodDetails) {
        Food updatedFood = foodService.updateFood(id, foodDetails);
        return ResponseEntity.ok(updatedFood);
    }

    // 7️⃣ Delete a food item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
    
    // 8️⃣ Search food items by name (case-insensitive, partial match)
    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFoodByName(@RequestParam String name) {
        List<Food> results = foodService.searchFoodByName(name);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Page<Food>> getPaginatedFood(
            @PageableDefault(size = 9, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(foodService.getPaginatedFood(pageable));
    }

    
}
