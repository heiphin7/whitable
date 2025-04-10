package com.api.whitable.controller;

import com.api.whitable.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.http.HttpRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/create")
    public String createReview() {
        // todo
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteReview() {
        // todo
        return "redirect:/";
    }

}
