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
public class ReservationDto {
    private Long reservationId;

    private Long flightNumber;

    private Long totalAmount;

    private LocalDate bookingDate;

    private Long passengersCount;

    private SeatType seatType;
    
    private ReservationStatus reservationStatus;
}
