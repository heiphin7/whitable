package com.api.whitable.service;

import com.api.whitable.dto.BookingDto;
import com.api.whitable.model.*;
import com.api.whitable.repository.BookingRepository;
import com.api.whitable.repository.RestaurantRepository;
import com.api.whitable.repository.ReviewRepository;
import com.api.whitable.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;


    public void create(BookingDto dto, Long userId, Long restaurantId) throws IllegalArgumentException {
        Booking bookingToSave = new Booking();
        bookingToSave.setGuestCount(dto.getGuestCount());

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с указанным ID не найден!")
        );

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new IllegalArgumentException("Ресторан с указанным ID не найден!")
        );

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = dto.getReservationDate();        // LocalDate
        String timeString = dto.getReservationTime();     // String, напр. "17:00"

        LocalTime time = LocalTime.parse(timeString, timeFormatter);     // превращаем в LocalTime
        LocalDateTime startTime = date.atTime(time);                     // объединяем дату и время
        LocalDateTime endTime = startTime.plusHours(1);                  // на 1 час позже

        bookingToSave.setStartTime(startTime);
        bookingToSave.setEndTime(endTime);
        bookingToSave.setBookingStatus(Status.PENDING); // Ожидается
        bookingToSave.setNote(dto.getNote());
        bookingToSave.setUser(user);
        bookingToSave.setRestaurant(restaurant);

        bookingRepository.save(bookingToSave);
    }

    public List<Booking> getBookingsByUserId(Long userId) throws IllegalArgumentException {
        // Здесь мы просто ищем пользователя чтобы удостовериться что при вызове не будет ошибки
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с указанным ID не найден!")
        );

        List<Booking> bookings = bookingRepository.findAllByUserId(userId);

        for (Booking booking : bookings) {
            if (booking.getEndTime().isBefore(LocalDateTime.now()) &&
                    booking.getBookingStatus() == Status.CONFIRMED) {

                booking.setBookingStatus(Status.COMPLETED);
                bookingRepository.save(booking); // сохраняем изменения
            }
        }

        return bookings;
    }

    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        assert booking != null;
        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Указанная бронь не принадлежит пользователю");
        }

        bookingRepository.delete(booking);
    }

    @Transactional
    public Map<String, Map<String, Integer>> getBookingsMapForRestaurant(Long restaurantId, LocalDate startDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = startDate.plusDays(14).atTime(23, 59, 59);

        // Получаем бронирования из базы
        List<Booking> bookings = bookingRepository.findBookingsByRestaurantAndStartTimeBetween(restaurantId, startDateTime, endDateTime);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Группируем бронирования по дате (yyyy-MM-dd) и времени (с округлением до минут)
        Map<String, Map<String, Integer>> bookingsMap = bookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getStartTime().toLocalDate().toString(),
                        Collectors.groupingBy(
                                booking -> booking.getStartTime().toLocalTime().format(timeFormatter),
                                Collectors.summingInt(Booking::getGuestCount)
                        )
                ));

        return bookingsMap;
    }

    public boolean canLeaveReview(Long userId, Long restaurantId) {
        // Проверяем, есть ли хотя бы одно завершённое бронирование
        List<Booking> bookings = bookingRepository.findByUserIdAndRestaurantIdAndBookingStatus(
                userId, restaurantId, Status.COMPLETED
        );

        // Если брони нет — сразу false
        if (bookings.isEmpty()) {
            return false;
        }

        // Проверяем, оставлял ли пользователь уже отзыв
        boolean alreadyLeftReview = reviewRepository.existsByUserIdAndRestaurantId(userId, restaurantId);

        // Можно оставить отзыв, если была завершённая бронь и отзыв ещё не оставляли
        return !alreadyLeftReview;
    }

}
