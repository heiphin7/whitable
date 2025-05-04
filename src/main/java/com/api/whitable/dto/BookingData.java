package com.api.whitable.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingData {
    private final LocalDateTime dateTime;
    private final Long count;

    public BookingData(LocalDateTime dateTime, Long count) {
        this.dateTime = dateTime;
        this.count    = count;
    }
}
