package com.ewallet.userservice.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketUpgradeRequestDto {

    private Long flightNumber;
    private String flightName;
    private LocalTime takeOffTime;
    private LocalTime landingTime;
    private Long paidAmount;
    private LocalDate date;
    private String source;
    private String destination;
    private String reservationId;
    private ReservationStatus reservationStatus;
    private Long amountToBePaid;
    private String paymentUrl;
    private Long seatsAvailable;
    private SeatStatus seatStatus;
    private List<TicketBookingResponseDto.BookingDetailPassengerWise> bookingDetailPassengerWises;


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
