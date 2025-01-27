package com.ewallet.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightAvailabilityDto {
    private String source;
    private String destination;
    private LocalDate date;
    private SeatType seatType;
}
