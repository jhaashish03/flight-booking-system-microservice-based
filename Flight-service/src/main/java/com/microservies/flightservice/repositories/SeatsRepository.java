package com.microservies.flightservice.repositories;

import com.microservies.flightservice.entities.SeatType;
import com.microservies.flightservice.entities.Seats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface SeatsRepository extends JpaRepository<Seats, UUID> {
    Seats findByFlightNumberAndDateIgnoreCase(Long flightNumber, String date);

    Seats findByFlightNumberAndDateAndSeatType(Long flightNumber, String date, SeatType
            seatType);

//    Seats findByFlightNumber(String flightNumber, FlightRunningStatus flightRunningStatus);

}