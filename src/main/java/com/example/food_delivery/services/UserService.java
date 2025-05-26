package com.example.food_delivery.services;

import com.example.food_delivery.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.food_delivery.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ Register a new user
    public User registerUser(User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        user.setRole("USER"); 
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // UserService.java
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    
}


