package com.api.whitable.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public String getAdminDashboardPage() {
        return "admin-dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String getAdminUsersPage() {
        return "admin-users";
    }
}
