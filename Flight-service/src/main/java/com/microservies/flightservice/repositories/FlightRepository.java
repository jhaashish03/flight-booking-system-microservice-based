package com.microservies.flightservice.repositories;

import com.microservies.flightservice.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
    List<Flight> findByDestinationLikeOrderByLandingTimeAsc(String destination);


    Flight findByFlightNumber(Long flightNumber);

    @Query("select f from Flight f where upper(f.flightName) like upper(concat('%', ?1, '%')) order by f.takeOffTime")
    List<Flight> findByFlightName(String flightName);

    @Query("""
            select f from Flight f
            where upper(f.source) = upper(?1) and upper(f.destination) = upper(?2)
            order by f.takeOffTime""")
    List<Flight> findBySrcAndDest(String source, String destination);

    @Query("select f from Flight f where upper(f.destination) = upper(?1) order by f.landingTime")
    List<Flight> findByDest(String destination);



}