package com.api.whitable.controller;

import com.api.whitable.dto.BookingDto;
import com.api.whitable.dto.CreateRestaurantDto;
import com.api.whitable.model.Amenity;
import com.api.whitable.model.Restaurant;
import com.api.whitable.model.Review;
import com.api.whitable.model.User;
import com.api.whitable.repository.ReviewLikeRepository;
import com.api.whitable.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant")
@Slf4j
public class RestaurantController {
    private final ReviewService reviewService;
    private final RestaurantService restaurantService;
    private final BookingService bookingService;
    private final JwtTokenService jwtTokenService;
    private final ReviewLikeRepository likeRepo;
    private final UserService userService;
    private final ReviewLikeService reviewLikeService;

    @GetMapping("/{restaurantId}")
    public String getRestaurantPage(@PathVariable("restaurantId") Long restaurantId, Model model,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    HttpServletRequest request) {
        Long userId = jwtTokenService.getUserIdFromToken(request);
        User currentUser = userService.findById(userId);
        Restaurant restaurant = restaurantService.findById(restaurantId).orElse(null);
        List<Review> restaurantReviews = reviewService.getRestaurantReviews(restaurantId);
        List<String> amenities = new ArrayList<>();
        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : LocalDate.now().plusDays(1);
        Map<String, Map<String, Integer>> bookings = bookingService.getBookingsMapForRestaurant(restaurantId, start);

        Map<Long, Long> helpfulCounts = restaurantReviews.stream()
                .collect(Collectors.toMap(
                        Review::getId,
                        reviewLikeService::countByReview  // long countByReview(Review review)
                ));

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
        model.addAttribute("helpfulCounts", helpfulCounts);
        model.addAttribute("userId", userId);

        model.addAttribute("likedReviews",
                likeRepo.findAllByUser(currentUser)          // List<ReviewLike>
                        .stream().map(l -> l.getReview().getId())
                        .collect(Collectors.toSet()));      // Set<Long>

        return "restaurant-profile";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createNewRestaurant(@ModelAttribute CreateRestaurantDto dto,
                                              @RequestParam("file") MultipartFile file) { // фото
        try {
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

