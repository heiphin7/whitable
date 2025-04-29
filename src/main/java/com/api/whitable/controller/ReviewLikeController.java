package com.api.whitable.controller;

import com.api.whitable.model.User;
import com.api.whitable.service.JwtTokenService;
import com.api.whitable.service.ReviewLikeService;
import com.api.whitable.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpRequest;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Slf4j
public class ReviewLikeController {

    private final ReviewLikeService likeService;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/{id}/helpful")
    public ReviewLikeDto toggleHelpful(@PathVariable Long id, HttpServletRequest request) {
        Long userId = jwtTokenService.getUserIdFromToken(request);
        User user = userService.findById(userId);

        ReviewLikeService.LikeResult r = likeService.toggle(id, user);
        return new ReviewLikeDto(r.isLiked(), r.getHelpfulCnt());
    }

    public record ReviewLikeDto(boolean liked, long helpfulCount) {}
}
