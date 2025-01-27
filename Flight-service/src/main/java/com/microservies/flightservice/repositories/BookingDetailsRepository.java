package com.microservies.flightservice.repositories;

import com.microservies.flightservice.entities.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingDetailsRepository extends JpaRepository<BookingDetails, UUID> {
}