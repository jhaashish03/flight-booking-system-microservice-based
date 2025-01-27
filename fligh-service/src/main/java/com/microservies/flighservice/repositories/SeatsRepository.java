package com.microservies.flighservice.repositories;

import com.microservies.flighservice.entities.SeatType;
import com.microservies.flighservice.entities.Seats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface SeatsRepository extends JpaRepository<Seats, UUID> {
    Seats findByFlightNumberAndDateIgnoreCase(Long flightNumber, String date);

    Seats findByFlightNumberAndDateAndSeatType(Long flightNumber, String date, SeatType seatType);

//    Seats findByFlightNumber(String flightNumber, FlightRunningStatus flightRunningStatus);

}