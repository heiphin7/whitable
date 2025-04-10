package com.api.whitable.repository;

import com.api.whitable.model.Booking;
import com.api.whitable.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT COALESCE(SUM(b.guestCount), 0) FROM Booking b WHERE b.restaurant = :restaurant " +
            "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
    int countByRestaurantAndTimeRange(
            @Param("restaurant") Restaurant restaurant,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<Booking> findAllByUserId(Long userId);
}
