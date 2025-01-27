package com.microservies.flightservice.repositories;

import com.microservies.flightservice.entities.Reservation;
import com.microservies.flightservice.entities.ReservationStatus;
import com.microservies.flightservice.entities.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findFirstByFlightNumberAndBookingDateAndSeatTypeAndReservationStatusAndCreatedDateGreaterThanOrderByCreatedDateAsc(Long flightNumber, LocalDate bookingDate, SeatType seatType, ReservationStatus reservationStatus, OffsetDateTime createdDate);

    List<Reservation> findByFlightNumberAndBookingDateOrderByCreatedDateAsc(Long flightNumber, LocalDate bookingDate);
/*    @Query("""
            select r from Reservation r
            where r.flightNumber = ?1 and r.bookingDate = ?2 and r.seatType = ?3 and r.reservationStatus = ?4
            order by r.createdDate""")
    List<Reservation> updateWaitingList(Long flightNumber, LocalDate bookingDate, SeatType seatType, ReservationStatus reservationStatus);*/

    @Query("""
            select r from Reservation r
            where r.flightNumber = ?1 and r.bookingDate = ?2 and r.seatType = ?3 and r.reservationStatus = ?4 and r.createdDate > ?5
            order by r.createdDate""")
    List<Reservation> updateWaitingListGreaterThan(Long flightNumber, LocalDate bookingDate,
                                                   SeatType seatType, ReservationStatus reservationStatus, OffsetDateTime createdDate);
}