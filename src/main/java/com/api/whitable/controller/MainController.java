package com.api.whitable.controller;

import com.api.whitable.dto.CreateRestaurantDto;
import com.api.whitable.dto.CreateUserDto;
import com.api.whitable.service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final RestaurantService restaurantService;

    @GetMapping("/")
    public String getMainPage() {
        return "index";
    }

    @GetMapping("/restaurants")
    public String restaurants(Model model, HttpServletRequest request) {
        model.addAttribute("isAdmin", request.getAttribute("isAdmin"));
        model.addAttribute("createRestaurantDto", new CreateRestaurantDto()); // Для создания
        model.addAttribute("restaurants", restaurantService.findAll());
        return "restaurants";
    }

    @GetMapping("/restaurant-profile")
    public String restaurantProfile() {
        return "restaurant-profile";
    }
}
