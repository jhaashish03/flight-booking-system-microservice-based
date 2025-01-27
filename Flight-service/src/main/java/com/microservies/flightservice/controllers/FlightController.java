package com.microservies.flightservice.controllers;

import com.microservies.flightservice.dtos.*;
import com.microservies.flightservice.entities.SeatType;
import com.microservies.flightservice.exceptions.FlightAlreadyExitsException;
import com.microservies.flightservice.services.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/"+FlightController.VERSION+"/flights")
public class FlightController {

    protected static final String VERSION="v1";
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/flight")
    public ResponseEntity<Void> addFlight(@RequestBody FlightDto flightDto) throws FlightAlreadyExitsException {
    flightService.addNewFlight(flightDto);
    return ResponseEntity.accepted().build();
    }

    @PostMapping("/availability")
    public ResponseEntity<FlightDetailsDto> getAvailability(@RequestBody FlightAvailabilityDto flightAvailabilityDto) {
        return ResponseEntity.ok(flightService.getAvailableFlights(flightAvailabilityDto));

    }

    @PostMapping("/tickets")
    public ResponseEntity<TicketBookingResponseDto> bookTickets(@RequestBody TicketBookingDto ticketBookingDto) {
        return ResponseEntity.ok(flightService.bookTickets(ticketBookingDto));
    }


    @GetMapping("/reservationStatus")
    public ResponseEntity<TicketBookingResponseDto> getReservationStatus(@RequestParam("reservationId")  String reservationId){
        return ResponseEntity.ok(flightService.getReservationStatus(reservationId));
    }

    @GetMapping("/flight")
    public ResponseEntity<FlightDto> getFlight(@RequestParam("flightId")  String flightId) {
        return ResponseEntity.ok(flightService.getFlightData(flightId));
    }
    @PostMapping("/upgradeBooking")
    public ResponseEntity<TicketBookingResponseDto> upgradeBooking(@RequestParam("reservationId")  String reservationId){
        return ResponseEntity.ok(flightService.upgradeBooking(reservationId));
    }

    @GetMapping("/availability/flight")
    public ResponseEntity<FlightDetail> getAvailabilityFlight(@RequestParam("flightId")  String flightId,@RequestParam("seatType") SeatType seatType,@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(flightService.getFlightAvailability(flightId,seatType,date));
    }

    @PostMapping("/canelReservation")
    public ResponseEntity<Void> cancelReservation(@RequestParam("reservationId")  String reservationId) {
        return ResponseEntity.ok(flightService.cancelBooking(reservationId));
    }

    @GetMapping("/reservationReport")
    public ResponseEntity<List<ReservationDto>> getReservationReport(@RequestParam("date")  String date,@RequestParam("destination") String destination) {
            return ResponseEntity.ok(flightService.getReservationReport(date,destination));
    }
}
