package com.api.whitable.service;

import com.api.whitable.dto.BookingDto;
import com.api.whitable.model.*;
import com.api.whitable.repository.BookingRepository;
import com.api.whitable.repository.RestaurantRepository;
import com.api.whitable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;


    public void create(BookingDto dto, Long userId, Long restaurantId) throws IllegalArgumentException {
        // TODO: Прочекать доступность типа да
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

        return bookingRepository.findAllByUserId(userId);
    }

    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        assert booking != null;
        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Указанная бронь не принадлежит пользователю");
        }

        bookingRepository.delete(booking);
    }
}
