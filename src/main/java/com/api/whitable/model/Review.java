package com.api.whitable.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private String title;
    private Integer rating;
    private String content;
    private LocalDate createdAt;

    /** Ленивая коллекция лайков */
    @OneToMany(mappedBy = "review",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<ReviewLike> likes = new HashSet<>();

    public int getHelpfulCount() {
        return likes.size();
    }
}
