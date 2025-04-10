package com.api.whitable.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "bookings")
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Связь с пользователем
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Связь с рестораном
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status bookingStatus;
    private String note;
    private Integer guestCount;
}
