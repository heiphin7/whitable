package com.api.whitable.service;

import com.api.whitable.dto.ReviewDto;
import com.api.whitable.model.Restaurant;
import com.api.whitable.model.Review;
import com.api.whitable.model.User;
import com.api.whitable.repository.RestaurantRepository;
import com.api.whitable.repository.ReviewRepository;
import com.api.whitable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public void create(ReviewDto dto, Long userId, Long restaurantId) throws NullPointerException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            throw new NullPointerException("Ресторан с указанным ID не найден");
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new NullPointerException("Указанный пользователь не найден!");
        }

        // Обновляем рейтинг ресторана
        Double updatedRating = calculateNewRating(dto.getRating(), restaurant.getRating(), restaurant.getRatingCount());
        Integer updatedCount = restaurant.getRatingCount() + 1;

        restaurant.setRating(updatedRating);
        restaurant.setRatingCount(updatedCount);

        Review reviewToSave = new Review();
        reviewToSave.setCreatedAt(LocalDate.now());
        reviewToSave.setUser(user);
        reviewToSave.setTitle(dto.getTitle());
        reviewToSave.setRating(dto.getRating());
        reviewToSave.setRestaurant(restaurant);
        reviewToSave.setContent(dto.getContent());
        reviewToSave.setHelpfulCount(0); // при инициализации 0 как обычно

        restaurantRepository.save(restaurant);
        reviewRepository.save(reviewToSave);
    }

    public void delete(ReviewDto dto, Long restaurantId) {
        Review review = reviewRepository.findById(dto.getId()).orElse(null);

        if (review == null) {
            throw new NullPointerException("Отзыва с указанным ID не существует");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            throw new NullPointerException("Ресторана с указанным ID не существует!");
        }

        Double updatedRating = calculateNewRatingAfterRemove(review.getRating(), restaurant.getRating(), restaurant.getRatingCount());
        Integer updatedCount = restaurant.getRatingCount() - 1;

        restaurant.setRating(updatedRating);
        restaurant.setRatingCount(updatedCount);

        restaurantRepository.save(restaurant);
        reviewRepository.delete(review);
    }

    public List<Review> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.getReviewsByRestaurantId(restaurantId);
    }

    private Double calculateNewRating(Integer newRating, Double restaurantRating, Integer ratingCount) {
        double totalRating = (restaurantRating * ratingCount) + newRating;
        int newRatingCount = ratingCount + 1;
        return totalRating / newRatingCount;
    }

    private Double calculateNewRatingAfterRemove(Integer removedRating, Double restaurantRating, Integer ratingCount) {
        double totalRating = restaurantRating * ratingCount;
        double newTotalRating = totalRating - removedRating;
        int newRatingCount = ratingCount - 1;
        return newTotalRating / newRatingCount;
    }
}
