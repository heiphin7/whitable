package com.api.whitable.controller;

import com.api.whitable.service.BookingService;
import com.api.whitable.service.ReviewService;
import com.api.whitable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    // TODO: Кароче нужно сделать чтобы карточки-статистики передавались либо через thymeleaf ну либо через подгрузку

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String getAdminDashboardPage() {
        return "admin-dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String getAdminUsersPage() {
        return "admin-users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public String getAdminBookingsPage() {
        return "admin-bookings";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/restaurants")
    public String getAdminRestaurantsPage() {
        return "admin-restaurants";
    }
}
