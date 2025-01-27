package com.microservies.flighservice.repositories;

import com.microservies.flighservice.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}