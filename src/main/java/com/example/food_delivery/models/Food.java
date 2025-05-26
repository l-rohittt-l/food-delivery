package com.example.food_delivery.models;

import jakarta.persistence.*;

@Entity
public class Food {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category; // "Vegetarian" or "Non Vegetarian"
    private String description;
    private double price;
    private String imageUrl; 
    private boolean active = true; // Default is true (food is visible to users)


    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	// Constructors
    public Food() {}

    public Food(String name, String category, String description, double price, String imageUrl) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
