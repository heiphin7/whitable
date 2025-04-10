package com.api.whitable.controller;

import com.api.whitable.dto.AuthDto;
import com.api.whitable.dto.CreateUserDto;
import com.api.whitable.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("authDto", new AuthDto());
        model.addAttribute("action", "login");
        return "auth";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("createUserDto", new CreateUserDto());
        model.addAttribute("action", "registration");
        return "auth";
    }

    @PostMapping("/registration")
    public String processRegistration(@ModelAttribute CreateUserDto dto,
                                      RedirectAttributes redirectAttributes) {
        log.info("Registration request: " + dto.toString());
        try {
            userService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Вы успешно зарегестрировались, теперь войдите в аккаунт");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/registration";
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка на стороне сервера");
            return "redirect:/registration";
        }
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute AuthDto dto,
                               RedirectAttributes redirectAttributes,
                               HttpServletResponse response, HttpServletRequest request) {
        try {
            String accessToken = userService.authenticate(dto, request, response);

            Cookie cookie = new Cookie("accessToken", accessToken);
            cookie.setPath("/");
            cookie.setMaxAge(86400); // 1d
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            return "redirect:/";
        } catch (BadCredentialsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Неправильный логин или пароль!");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Ошибка при аутентификации пользователя: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка на стороне сервера");
            return "redirect:/login";
        }
    }
}
