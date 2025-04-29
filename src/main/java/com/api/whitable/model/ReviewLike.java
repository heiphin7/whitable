package com.api.whitable.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","review_id"}))
@Data
public class ReviewLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    private LocalDateTime likedAt = LocalDateTime.now();

    public ReviewLike(User user, Review review) {
        this.user = user;
        this.review = review;
        this.likedAt = LocalDateTime.now();
    }

    public ReviewLike() {

    }
}
