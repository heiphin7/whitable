package com.api.whitable.controller;

import com.api.whitable.service.BookingService;
import com.api.whitable.service.RestaurantService;
import com.api.whitable.service.ReviewService;
import com.api.whitable.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final RestaurantService restaurantService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String getAdminDashboardPage(Model model) {
        model.addAttribute("usersCount", userService.getUsersCount());
        model.addAttribute("bookingsCount", bookingService.getBookingsCount());
        model.addAttribute("reviewsCount", reviewService.getReviewsCount());
        model.addAttribute("restaurantsCount", restaurantService.getRestaurantsCount());
        return "admin-dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String getAdminUsersPage(Model model) {
        model.addAttribute("usersCount", userService.getUsersCount());
        model.addAttribute("reviewsCount", reviewService.getReviewsCount());
        return "admin-users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public String getAdminBookingsPage(Model model) {
        model.addAttribute("bookingsCount", bookingService.getBookingsCount());
        model.addAttribute("reviewsCount", reviewService.getReviewsCount());
        model.addAttribute("lastWeekBookingsCount", bookingService.getLastWeekBookingsCount());
        return "admin-bookings";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/restaurants")
    public String getAdminRestaurantsPage(Model model) {
        model.addAttribute("restaurantsCount", restaurantService.getAllRestaurants());
        model.addAttribute("reviewsCount", reviewService.getReviewsCount());
        model.addAttribute("bookingsCount", bookingService.getBookingsCount());
        return "admin-restaurants";
    }
}
