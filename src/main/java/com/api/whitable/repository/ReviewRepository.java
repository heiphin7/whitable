package com.api.whitable.repository;

import com.api.whitable.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> getReviewsByRestaurantId(Long restaurantId);
    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);
}
