package com.ewallet.userservice.services.client;

import com.ewallet.userservice.configurations.feignConfig.FeignConfig;
import com.ewallet.userservice.configurations.feignConfig.FeignErrorDecoder;
import com.ewallet.userservice.dtos.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "flight-service",configuration = FeignConfig.class)
public interface FlightServiceOpenFeignClient {
    static final String VERSION = "v1";

    @PostMapping("/" + VERSION + "/flights/flight")
    public ResponseEntity<Void> addFlight(@RequestBody FlightCreationDto flightDto);


    @PostMapping("/" + VERSION + "/flights/availability")
    public ResponseEntity<FlightDetailsDto> getAvailability(@RequestBody FlightAvailabilityDto flightAvailabilityDto);


    @PostMapping("/" + VERSION + "/flights/tickets")
    public ResponseEntity<TicketBookingResponseDto> bookTickets(@RequestBody TicketBookingDto ticketBookingDto);

    @GetMapping("/" + VERSION + "/flights/reservationStatus")
    public ResponseEntity<TicketBookingResponseDto> getReservationStatus(@RequestParam("reservationId") String reservationId);

    @GetMapping("/" + VERSION + "/flights/flight")
    public ResponseEntity<FlightDto> getFlight(@RequestParam("flightId") String flightId);


    @GetMapping("/" + VERSION + "/flights/availability/flight")
    public ResponseEntity<FlightDetail> getAvailabilityFlight(@RequestParam("flightId") String flightId, @RequestParam("seatType") SeatType seatType, @RequestParam("date") LocalDate date);

    @PostMapping("/" + VERSION + "/flights/upgradeBooking")
    public ResponseEntity<TicketBookingResponseDto> upgradeBooking(@RequestParam("reservationId") String reservationId);

    @PostMapping("/" + VERSION + "/flights/canelReservation")
    public ResponseEntity<Void> cancelReservation(@RequestParam("reservationId") String reservationId);

    @GetMapping("/" + VERSION + "/flights/reservationReport")
    public ResponseEntity<List<ReservationDto>> getReservationReport(@RequestParam("date")  String date, @RequestParam("destination") String destination);

}