package com.api.whitable.controller;

import com.api.whitable.dto.BookingDto;
import com.api.whitable.model.Booking;
import com.api.whitable.service.BookingService;
import com.api.whitable.service.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/create")
    public String createBooking(@ModelAttribute BookingDto dto, HttpServletRequest request,
                                @RequestParam("restaurantId") Long restaurantId) {
        Long userId = jwtTokenService.getUserIdFromToken(request);
        bookingService.create(dto, userId, restaurantId);

        return "redirect:/my-bookings";
    }

    @GetMapping("/my-bookings")
    public String getUserBookings(HttpServletRequest request, Model model) {
        Long userId = jwtTokenService.getUserIdFromToken(request);
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);

        model.addAttribute("bookings", bookings);
        return "my-bookings";
    }

    @PostMapping("/cancel")
    public String cancelBookings(HttpServletRequest request,
                                 @RequestParam("bookingId") Long bookingId) {
        Long userId = jwtTokenService.getUserIdFromToken(request);
        bookingService.cancelBooking(bookingId, userId);
        return "redirect:/bookings/my-bookings";
    }

}
