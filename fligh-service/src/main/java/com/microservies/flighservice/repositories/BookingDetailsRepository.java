package com.microservies.flighservice.repositories;

import com.microservies.flighservice.entities.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingDetailsRepository extends JpaRepository<BookingDetails, UUID> {
}