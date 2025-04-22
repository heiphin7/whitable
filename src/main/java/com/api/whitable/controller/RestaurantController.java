package com.api.whitable.controller;

import com.api.whitable.dto.BookingDto;
import com.api.whitable.dto.CreateRestaurantDto;
import com.api.whitable.model.Amenity;
import com.api.whitable.model.Restaurant;
import com.api.whitable.model.Review;
import com.api.whitable.service.BookingService;
import com.api.whitable.service.JwtTokenService;
import com.api.whitable.service.RestaurantService;
import com.api.whitable.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant")
@Slf4j
public class RestaurantController {
    private final ReviewService reviewService;
    private final RestaurantService restaurantService;
    private final BookingService bookingService;
    private final JwtTokenService jwtTokenService;

    @GetMapping("/{restaurantId}")
    public String getRestaurantPage(@PathVariable("restaurantId") Long restaurantId, Model model,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    HttpServletRequest request) {
        Long userId = jwtTokenService.getUserIdFromToken(request);
        Restaurant restaurant = restaurantService.findById(restaurantId).orElse(null);
        List<Review> restaurantReviews = reviewService.getRestaurantReviews(restaurantId);
        List<String> amenities = new ArrayList<>();
        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : LocalDate.now().plusDays(1);
        Map<String, Map<String, Integer>> bookings = bookingService.getBookingsMapForRestaurant(restaurantId, start);

        for(Amenity a: restaurant.getAmenities()) {
            amenities.add(a.getName());
        }

        if (restaurant == null) {
            return "redirect:/404";
        }

        // Добавить в model всю инфу
        model.addAttribute("restaurant", restaurant);

        // Преимущества, Отзывы и Типы кухни
        model.addAttribute("amenities", amenities);
        model.addAttribute("reviews", restaurantReviews);
        model.addAttribute("cuisineTypes", restaurant.getCuisineType());
        model.addAttribute("bookingDto", new BookingDto());
        model.addAttribute("openingHours", restaurant.getOpeningHours());
        model.addAttribute("bookings", bookings);
        model.addAttribute("capacity", restaurant.getCapacityForHour());
        model.addAttribute("canReview", bookingService.canLeaveReview(userId, restaurantId));

        log.info("can review: " + bookingService.canLeaveReview(userId, restaurantId));
        return "restaurant-profile";
    }

    @PostMapping("/create")
    public String createNewRestaurant(@ModelAttribute CreateRestaurantDto dto,
                                              @RequestParam("file") MultipartFile file) { // фото
        try {
            log.info("Opening hours: " + dto.getOpeningHours());
            restaurantService.create(dto, file);
        } catch (IOException e) {
            log.error("Ошибка при ссоздании Ресторана, ошибка связана с фото: " + e.getMessage() + " " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Ошибка при создании Ресторана: " + dto.toString() + " " + e.getMessage());
        }

        return "redirect:/restaurants";
    }

}

