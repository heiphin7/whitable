package com.api.whitable.repository;

import com.api.whitable.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);
    Optional<Restaurant> findByName(String name);
    @Query(value = "SELECT * FROM restaurants WHERE id <> :excludeId ORDER BY RANDOM() LIMIT 9", nativeQuery = true)
    List<Restaurant> findRandomRestaurantsExcludingId(@Param("excludeId") Long excludeId);
}
