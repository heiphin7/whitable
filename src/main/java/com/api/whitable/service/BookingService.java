package com.api.whitable.service;

import com.api.whitable.dto.BookingData;
import com.api.whitable.dto.BookingDto;
import com.api.whitable.dto.BookingInfoDto;
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
import java.util.ArrayList;
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
        // Убеждаемся, что пользователь существует
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с указанным ID не найден!")
        );

        List<Booking> bookings = bookingRepository.findAllByUserId(userId);

        LocalDateTime now = LocalDateTime.now();
        for (Booking booking : bookings) {
            // Если бронирование закончилось и статус ещё не COMPLETED и не CANCELLED
            if (booking.getEndTime().isBefore(now)
                    && booking.getBookingStatus() != Status.COMPLETED
                    && booking.getBookingStatus() != Status.CANCELED) {

                booking.setBookingStatus(Status.COMPLETED);
                bookingRepository.save(booking);
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
        List<Booking> bookings = bookingRepository.findByUserIdAndRestaurantId(
                userId, restaurantId
        );

        // Если брони нет — сразу false]
        if (bookings.isEmpty()) {
            return false;
        }

        boolean canReview = bookings.stream()
                .anyMatch(b -> b.getBookingStatus() == Status.COMPLETED);

        if (!canReview) {
            return false;
        }

        // Проверяем, оставлял ли пользователь уже отзыв
        boolean alreadyLeftReview = reviewRepository.existsByUserIdAndRestaurantId(userId, restaurantId);

        // Можно оставить отзыв, если была завершённая бронь и отзыв ещё не оставляли
        return !alreadyLeftReview;
    }

    public List<BookingInfoDto> getAllBookingDtos() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingInfoDto> dtos = new ArrayList<>();

        for (Booking b: bookings) {
            BookingInfoDto bookingDto = BookingInfoDto.builder()
                    .id(b.getId())
                    .guestCount(b.getGuestCount())
                    .name(b.getUser().getUsername())
                    .reservationTime(b.getStartTime())
                    .restaurantName(b.getRestaurant().getName())
                    .status(b.getBookingStatus().toString())
                    .build();

            dtos.add(bookingDto);
        }

        return dtos;
    }

    public void updateBookingStatus(String updatedStatus, Long bookingId) throws IllegalArgumentException {
        Status status = null;
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            throw new IllegalArgumentException("Бронирование с указанным ID не найдено!");
        }

        switch (updatedStatus) {
            case "PENDING":
                status = Status.PENDING;
                break;
            case "CONFIRMED":
                status = Status.CONFIRMED;
                break;
            case "CANCELED":
                status = Status.CANCELED;
                break;
            case "COMPLETED":
                status = Status.COMPLETED;
                break;
            default:
                throw new IllegalArgumentException("Обновленного статуса не существует в базе данных!");
        }

        booking.setBookingStatus(status);
        bookingRepository.save(booking);
    }

    public long getBookingsCount() {
        return bookingRepository.count();
    }

    public List<BookingData> getBookingsData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(16).with(LocalTime.MIN);
        LocalDateTime end   = now.plusDays(14).with(LocalTime.MAX);

        return bookingRepository.countByDayBetween(start, end);
    }

    public long getLastWeekBookingsCount() {
        LocalDateTime end   = LocalDateTime.now().with(LocalTime.MAX);
        LocalDateTime start = end.minusDays(6).with(LocalTime.MIN);
        return bookingRepository.countByStartTimeBetween(start, end);
    }

    public List<Booking> getLastBookings() {
        return bookingRepository.findTop5ByOrderByStartTimeDesc();
    }
}
