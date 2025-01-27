package com.ewallet.userservice.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketBookingResponseDto implements Serializable {


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
