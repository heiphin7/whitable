package com.api.whitable.service;

import com.api.whitable.dto.AuthDto;
import com.api.whitable.dto.CreateUserDto;
import com.api.whitable.model.User;
import com.api.whitable.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

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
}
