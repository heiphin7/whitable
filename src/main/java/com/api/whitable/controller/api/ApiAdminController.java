package com.api.whitable.controller.api;

import com.api.whitable.dto.ChangeUsernameEmailDto;
import com.api.whitable.service.BookingService;
import com.api.whitable.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Slf4j
public class ApiAdminController {
    private final UserService userService;
    private final BookingService bookingService;

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
    public ResponseEntity<?> updateBookingStatus(@RequestBody String updatedStatus,
                                                 @RequestBody Long bookingId) {
        try {
            bookingService.updateBookingStatus(updatedStatus, bookingId);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Статус успешно обновлен!");
    }

}
