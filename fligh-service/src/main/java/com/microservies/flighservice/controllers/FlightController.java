package com.microservies.flighservice.controllers;

import com.microservies.flighservice.dtos.*;
import com.microservies.flighservice.services.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/"+FlightController.VERSION+"/flights")
public class FlightController {

    protected static final String VERSION="v1";
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping("/flight")
    public ResponseEntity<Void> addFlight(@RequestBody FlightDto flightDto) {
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


}
