package com.api.whitable.service;

import com.api.whitable.model.Review;
import com.api.whitable.model.ReviewLike;
import com.api.whitable.model.User;
import com.api.whitable.repository.ReviewLikeRepository;
import com.api.whitable.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewLikeService {

    private final ReviewLikeRepository likeRepo;
    private final ReviewRepository reviewRepo;

    /** true → поставили лайк, false → убрали */
    public LikeResult toggle(Long reviewId, User currentUser) {
        Review review = reviewRepo.getReferenceById(reviewId);

        if (review.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Нельзя отмечать собственный отзыв как полезный");
        }

        return likeRepo.findByUserAndReview(currentUser, review)
                .map(like -> {                 // лайк уже есть → убираем
                    likeRepo.delete(like);
                    return new LikeResult(false, likeRepo.countByReview(review));
                })
                .orElseGet(() -> {            // лайка нет → ставим
                    likeRepo.save(new ReviewLike(currentUser, review));
                    return new LikeResult(true, likeRepo.countByReview(review));
                });
    }

    public long countByReview(Review review) {
        return likeRepo.countByReview(review);
    }

    @Getter
    public static class LikeResult {
        boolean liked;
        long helpfulCnt;

        public LikeResult(boolean liked, long cnt) {
            this.liked = liked;
            this.helpfulCnt = cnt;
        }

    }
}
