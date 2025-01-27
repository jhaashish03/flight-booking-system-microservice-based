package com.microservies.flightservice.dtos;

import com.microservies.flightservice.entities.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
 public  class FlightDetail {
        private String flightNumber;
        private String flightName;
        private LocalTime takeOffTime;
        private LocalTime landingTime;
        private Long fare;
        private Long seats;
        private SeatStatus seatStatus;





}