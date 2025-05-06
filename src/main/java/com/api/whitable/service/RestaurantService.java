package com.api.whitable.service;

import com.api.whitable.dto.CreateRestaurantDto;
import com.api.whitable.dto.RestaurantDto;
import com.api.whitable.dto.RestaurantInfoDto;
import com.api.whitable.model.Amenity;
import com.api.whitable.model.CuisineType;
import com.api.whitable.model.Restaurant;
import com.api.whitable.repository.AmenityRepository;
import com.api.whitable.repository.CuisineTypeRepository;
import com.api.whitable.repository.RestaurantRepository;
import com.api.whitable.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final AmenityRepository amenityRepository;
    private final ReviewRepository reviewRepository;
    private final CuisineTypeRepository cuisineTypeRepository;

    @Transactional
    public void create(CreateRestaurantDto dto, MultipartFile file) throws IllegalArgumentException, IOException {
        validateField(dto, "dto");
        validateField(file, "file");
        validateField(dto.getName(), "name");
        validateField(dto.getAverageCheck(), "averageCheck");
        validateField(dto.getDescription(), "description");
        validateField(dto.getOpeningHours(), "openingHours");
        validateField(dto.getAddress(), "address");
        validateField(dto.getPhoneNumber(), "phoneNumber");

        List<Amenity> amenities = new ArrayList<>();

        // Проверка, все ли указанные удобства существуют
        for (String amenity : dto.getFacilities()) {
            Amenity amenityInDb = amenityRepository.findByName(amenity);
            if (amenityInDb == null) {
                throw new IllegalArgumentException("Вы указали неправильное \"Удобство\". Примеры: Уличные сиденья, Парковка, Wi-Fi, Доставка, Самовывоз. Вы указали: " + amenity);
            }
            amenities.add(amenityInDb);
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPhoneNumber(dto.getPhoneNumber());
        restaurant.setAmenities(amenities);
        restaurant.setAverageCheck(dto.getAverageCheck());
        restaurant.setCuisineType(dto.getCuisineType());
        restaurant.setCapacityForHour(dto.getCapacityForHour());

        if (dto.getUrlToRestaurant() != null) {
            restaurant.setUrlToRestaurants(dto.getUrlToRestaurant());
        }

        Map<String, String> processedOpeningHours = new HashMap<>();
        dto.getOpeningHours().forEach((day, times) -> {
            String start = times.get("start");
            String end = times.get("end");
            if (start == null || start.isEmpty() || end == null || end.isEmpty()) {
                processedOpeningHours.put(day, "Closed");
            } else {
                processedOpeningHours.put(day, start + " - " + end);
            }
        });
        restaurant.setOpeningHours(processedOpeningHours);

        // Проверка файла на пустоту
        if (file.isEmpty()) {
            throw new IllegalArgumentException("file");
        }

        // Директория для сохранения фото (обязательно измените для своего окружения)
        String uploadDir = "C:\\Users\\rshal\\OneDrive\\Рабочий стол\\whitable\\src\\main\\resources\\static\\img\\";
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        restaurant.setPhotoUrl("/img/" + fileName); // Путь для фронтенда
        restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> findById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }

    private void validateField(Object field, String fieldName) {
        if (field == null || (field instanceof String && ((String) field).isEmpty())) {
            throw new IllegalArgumentException(fieldName);
        }
    }

    public List<RestaurantDto> findAllDto() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<RestaurantDto> dtos = new ArrayList<>();

        for(Restaurant r: restaurants) {
            Integer averageCheck = r.getAverageCheck();
            String price;

            if (averageCheck != null) {
                if (averageCheck <= 5000) {
                    price = "$";
                } else if (averageCheck <= 8000) {
                    price = "$$";
                } else if (averageCheck <= 10000) {
                    price = "$$$";
                } else {
                    price = "$$$$";
                }
            } else {
                price = "N/A"; // Если средний чек не указан
            }

            List<String> features = new ArrayList<>();
            for (Amenity a: r.getAmenities()) {
                features.add(a.getName());
            }


            RestaurantDto dto = new RestaurantDto(r.getId(), r.getName(), r.getPhotoUrl(),
                    r.getDescription(), r.getRating(), price, r.getCuisineType(),
                    r.getAddress(), features
            );

            dtos.add(dto);
        }

        return dtos;
    }

    public List<RestaurantInfoDto> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<RestaurantInfoDto> dtos = new ArrayList<>();

        for (Restaurant r: restaurants) {
            RestaurantInfoDto dto = RestaurantInfoDto.builder()
                    .id(r.getId())
                    .url(r.getUrlToRestaurants())
                    .address(r.getAddress())
                    .description(r.getDescription())
                    .name(r.getName())
                    .cuisineType(r.getCuisineType())
                    .phoneNumber(r.getPhoneNumber())
                    .reviewCount(reviewRepository.countByRestaurant(r))
                    .rating(r.getRating())
                    .build();

            dtos.add(dto);
        }

        return dtos;
    }

    public void updateRestaurantInfo(RestaurantInfoDto dto, Long restaurantId) throws IllegalArgumentException {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new IllegalArgumentException("Ресторан с указанным ID не найден!")
        );

        // Если название поменяли, смотрим - допустен ли он
        if (!restaurant.getName().equals(dto.getName())) {
            Restaurant checkName = restaurantRepository.findByName(dto.getName()).orElse(null);

            if (checkName != null) {
                throw new IllegalArgumentException("Указанное имя для ресторана занято!");
            }
        }
        CuisineType cuisineType = cuisineTypeRepository.findByName(dto.getCuisineType());

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPhoneNumber(dto.getPhoneNumber());
        restaurant.setCuisineType(cuisineType.getName());
        restaurant.setUrlToRestaurants(dto.getUrl());

        restaurantRepository.save(restaurant);
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public long getRestaurantsCount() {
        return restaurantRepository.count();
    }

    public List<Restaurant> getRelatedRestaurants(Long restaurantId) {
        return restaurantRepository.findRandomRestaurantsExcludingId(restaurantId);
    }
}
