package com.api.whitable.repository;

import com.api.whitable.model.Review;
import com.api.whitable.model.ReviewLike;
import com.api.whitable.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike,Long> {
    boolean existsByUserAndReview(User user, Review review);
    Optional<ReviewLike> findByUserAndReview(User user, Review review);
    long countByReview(Review review);
    @Query("select rl.review.id from ReviewLike rl where rl.user = :user")
    Set<Long> findReviewIdsByUser(@Param("user") User user);
    List<ReviewLike> findAllByUser(User user);
}
