package com.api.whitable.controller.api;

import com.api.whitable.dto.BookingStatusDto;
import com.api.whitable.dto.ChangeUsernameEmailDto;
import com.api.whitable.dto.RestaurantInfoDto;
import com.api.whitable.service.BookingService;
import com.api.whitable.service.RestaurantService;
import com.api.whitable.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ApiAdminController {
    private final UserService userService;
    private final BookingService bookingService;
    private final RestaurantService restaurantService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUserDto());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users")
    public ResponseEntity<?> changeUsernameAndEmail(@RequestBody ChangeUsernameEmailDto dto) {
        try {
            userService.changeUsernameAndEmail(dto);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Вы успешно изменили Username & Email");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookingDtos());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bookings")
    public ResponseEntity<?> updateBookingStatus(@RequestBody BookingStatusDto dto) {
        try {
            bookingService.updateBookingStatus(dto.getUpdatedStatus(), dto.getBookingId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.ok(Map.of("message", "Статус успешно обновлен!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}")
    public ResponseEntity<?> updateRestaurantInfo(@RequestBody RestaurantInfoDto dto,
                                                  @PathVariable Long id) {
        try {
            restaurantService.updateRestaurantInfo(dto, id);
            return ResponseEntity.ok("Ресторан успешно обновлен!");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.info("/api/restaurants/{id} ERROR: " + e.getMessage());
            return new ResponseEntity<>("Server Error :(" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings/data")
    public ResponseEntity<?> getBookingsData() {
        return ResponseEntity.ok(bookingService.getBookingsData());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings/lastBookings")
    public ResponseEntity<?> getLastBookings() {
        return ResponseEntity.ok(bookingService.getLastBookings());
    }
}
