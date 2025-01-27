package com.microservies.flightservice.repositories;

import com.microservies.flightservice.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
}