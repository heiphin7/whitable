package com.api.whitable.service;

import com.api.whitable.dto.AuthDto;
import com.api.whitable.dto.ChangeUsernameEmailDto;
import com.api.whitable.dto.CreateUserDto;
import com.api.whitable.dto.UserDto;
import com.api.whitable.model.User;
import com.api.whitable.repository.BookingRepository;
import com.api.whitable.repository.ReviewRepository;
import com.api.whitable.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public void save(CreateUserDto dto) throws IllegalArgumentException {
        if (dto == null) {
            throw new IllegalArgumentException("Заполните все поля!");
        }

        // Проверка всех полей
        if (
                dto.getUsername().isEmpty() || dto.getEmail().isEmpty() || dto.getPassword().isEmpty()
        ) {
            throw new IllegalAccessError("Заполните все поля!");
        }

        User checkEmail = userRepository.findByEmail(dto.getEmail());

        if (checkEmail != null) {
            throw new IllegalArgumentException("Указанная почта занята!");
        }

        User checkUsername = userRepository.findByUsername(dto.getUsername());

        if (checkUsername != null) {
            throw new IllegalArgumentException("Имя пользователя занято!");
        }

        // Если все окей, то просто сохраняем пользователя
        User userToSave = new User();
        userToSave.setUsername(dto.getUsername());
        userToSave.setEmail(dto.getEmail());

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        userToSave.setPassword(encodedPassword);
        userRepository.save(userToSave);
    }

    public String authenticate(AuthDto dto, HttpServletRequest request, HttpServletResponse response) throws BadCredentialsException {
        User user = userRepository.findByEmail(dto.getEmail());

        if (user == null) {
            throw new BadCredentialsException("Имя пользователя или пароль неправильный!");
        }

        // Сравниваем зашифрованный и переданные пароли с помощью специального метода
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Имя пользователя или пароль неправильный!");
        }

        jwtTokenService.generateTokens(request, response, dto); // генерация и сохранение refresh
        return jwtTokenService.generateAccessToken(dto.getEmail());
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<UserDto> getAllUserDto() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user: users) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());
            userDto.setUsername(user.getUsername());
            userDto.setIsAdmin(user.getIsAdmin());

            userDto.setReviewCount(reviewRepository.countByUserId(user.getId()));
            userDto.setBookingCount(bookingRepository.countByUserId(user.getId()));

            userDtos.add(userDto);
        }

        return userDtos;
    }

    public void changeUsernameAndEmail(ChangeUsernameEmailDto dto) throws IllegalArgumentException {
        User userToChange = userRepository.findById(dto.getId()).orElse(null);
        System.out.println("Данные для изменения: " + dto);
        System.out.println("Пользователь в базе данных: " + userToChange);

        if (!userToChange.getUsername().equals(dto.getUsername())) {
            User checkUsername = userRepository.findByUsername(dto.getUsername());
            if (checkUsername != null) {
                throw new IllegalArgumentException("Данное имя пользователя уже занято!");
            }
        }

        if (!userToChange.getEmail().equals(dto.getEmail())) {
            User checkEmail = userRepository.findByEmail(dto.getEmail());
            if (checkEmail != null) {
                throw new IllegalArgumentException("Данная почта уже занята!");
            }
        }

        userToChange.setUsername((dto.getUsername()));
        userToChange.setEmail(dto.getEmail());
        userRepository.save(userToChange);
    }
}
