package com.microservies.flighservice.dtos;

import com.microservies.flighservice.entities.ReservationStatus;
import com.microservies.flighservice.entities.SeatType;
import com.microservies.flighservice.entities.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketBookingResponseDto {

    private String reservationId;
    private ReservationStatus reservationStatus;
    private List<BookingDetailPassengerWise> bookingDetailPassengerWises;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingDetailPassengerWise{
        private PassengerDto passenger;
        private TicketStatus ticketStatus;
        private SeatType seatType;
        private Long seatNumber;
    }
}
