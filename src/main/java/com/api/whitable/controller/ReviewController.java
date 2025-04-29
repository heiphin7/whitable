package com.api.whitable.controller;

import com.api.whitable.dto.ReviewDto;
import com.api.whitable.service.JwtTokenService;
import com.api.whitable.service.ReviewLikeService;
import com.api.whitable.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
@Slf4j
public class ReviewController {
    private final JwtTokenService jwtTokenService;
    private final ReviewService reviewService;

    @PostMapping("/create")
    public String createReview(ReviewDto dto, HttpServletRequest request,
                               @RequestParam("restaurantId") Long restaurantId) {
        Long userId = jwtTokenService.getUserIdFromToken(request);
        reviewService.create(dto, userId, restaurantId);
        log.info("Request to create review: " + dto + " userId: " + userId + " restaurantId: " + restaurantId);
        return "redirect:/restaurant/" + restaurantId;
    }

    @PostMapping("/delete")
    public String deleteReview() {
        // todo потом сам сделаю, пока не трогай это
        return "redirect:/";
    }
}
