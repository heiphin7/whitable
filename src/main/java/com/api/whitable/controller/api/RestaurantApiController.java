package com.api.whitable.controller.api;

import com.api.whitable.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
@Slf4j
public class RestaurantApiController {

    private final RestaurantService restaurantService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAllRestaurants() {
        return ResponseEntity.ok(restaurantService.findAllDto());
    }

    @GetMapping("/find/{restaurantId}")
    public ResponseEntity<?> findByRestaurantId(@PathVariable("restaurantId") Long restaurantId) {
        return ResponseEntity.ok(restaurantService.findById(restaurantId));
    }
}
/*
Salter's
Добро пожаловать в галерею гастрономических шедевров - SALTER'S.
Территория изощренных вкусов откроется в отеле Sheraton Nur-Sultan 20 мая 2022 года
Воскресенье : Закрыто \n Вторник - Суббота: 18:00-2:00
Улица Сыганак 60/1 Sheraton Astana Hotel
+77713878026
averageCheck=15000
https://www.instagram.com/salters.restaurant/

*/