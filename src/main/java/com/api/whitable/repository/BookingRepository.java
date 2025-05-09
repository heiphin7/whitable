package com.api.whitable.repository;

import com.api.whitable.dto.BookingData;
import com.api.whitable.model.Booking;
import com.api.whitable.model.Restaurant;
import com.api.whitable.model.Status;
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

    @Query("SELECT b FROM Booking b " +
            "WHERE b.restaurant.id = :restaurantId " +
            "AND b.startTime BETWEEN :start AND :end")
    List<Booking> findBookingsByRestaurantAndStartTimeBetween(@Param("restaurantId") Long restaurantId,
                                                              @Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end);

    List<Booking> findAllByUserId(Long userId);

    List<Booking> findByUserIdAndRestaurantIdAndBookingStatus(Long userId, Long restaurantId, Status bookingStatus);
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT new com.api.whitable.dto.BookingData(
           b.startTime,
           COUNT(b)
        )
        FROM Booking b
        WHERE b.startTime BETWEEN :from AND :to
        GROUP BY b.startTime
        ORDER BY b.startTime
    """)
    List<BookingData> countByDayBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );

    long countByStartTimeBetween(LocalDateTime from, LocalDateTime to);

    List<Booking> findTop5ByOrderByStartTimeDesc();
}
