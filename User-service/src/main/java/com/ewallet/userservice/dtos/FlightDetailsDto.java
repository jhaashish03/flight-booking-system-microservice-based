package com.ewallet.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightDetailsDto {
    private String source;
    private String destination;
    private LocalDate date;
    private SeatType seatType;
    private List<FlightDetail> flightDetailList;


}
