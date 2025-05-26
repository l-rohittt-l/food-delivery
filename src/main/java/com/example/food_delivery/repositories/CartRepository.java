package com.example.food_delivery.repositories;

import com.example.food_delivery.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    List<Cart> findBySessionId(String sessionId);

    Optional<Cart> findByFoodIdAndSessionId(Long foodId, String sessionId);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);
    
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.food.id = :foodId AND c.sessionId = :sessionId")
    void deleteByFoodIdAndSessionId(@Param("foodId") Long foodId, @Param("sessionId") String sessionId);


}
