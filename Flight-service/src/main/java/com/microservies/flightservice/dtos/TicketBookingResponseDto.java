package com.microservies.flightservice.dtos;

import com.microservies.flightservice.entities.ReservationStatus;
import com.microservies.flightservice.entities.SeatType;
import com.microservies.flightservice.entities.TicketStatus;
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
public class TicketBookingResponseDto {
    private Long flightNumber;
    private String flightName;
    private LocalTime takeOffTime;
    private LocalTime landingTime;
    private Long totalFare;
    private LocalDate date;
    private String source;
    private String destination;
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
