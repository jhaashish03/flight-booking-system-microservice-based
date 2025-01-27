package com.microservies.flightservice.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor@Builder
public class FlightDto {
    private Long flightNumber;
    private String flightName;
    private String source;
    private String destination;
    private LocalTime takeOffTime;
    private LocalTime landingTime;
    private Long economyFare;
    private Long businessFare;
    private Long totalSeats;
}
