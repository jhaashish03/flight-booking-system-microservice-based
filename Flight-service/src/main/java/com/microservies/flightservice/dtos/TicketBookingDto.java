package com.microservies.flightservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microservies.flightservice.entities.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketBookingDto {
    private Long flightNumber;
    private String flightName;
    private LocalTime takeOffTime;
    private LocalTime landingTime;
    private Long fare;
    private LocalDate date;
    private String source;
    private String destination;
    private String category;
    private SeatType seatType;
    private List<PassengerDto> passengerDtos;


}
