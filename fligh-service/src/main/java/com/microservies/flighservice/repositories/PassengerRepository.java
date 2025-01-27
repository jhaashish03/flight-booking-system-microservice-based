package com.microservies.flighservice.repositories;

import com.microservies.flighservice.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
}