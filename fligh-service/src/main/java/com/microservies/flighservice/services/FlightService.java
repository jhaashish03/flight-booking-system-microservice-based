package com.microservies.flighservice.services;

import com.microservies.flighservice.dtos.*;
import com.microservies.flighservice.entities.*;
import com.microservies.flighservice.mappers.FlightMapper;
import com.microservies.flighservice.repositories.BookingDetailsRepository;
import com.microservies.flighservice.repositories.FlightRepository;
import com.microservies.flighservice.repositories.ReservationRepository;
import com.microservies.flighservice.repositories.SeatsRepository;
import com.microservies.flighservice.services.util.FlightUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final SeatsRepository seatsRepository;
    private final FlightMapper flightMapper;
    private final FlightUtil flightUtil;
    private final ReservationRepository reservationRepository;
    private final BookingDetailsRepository bookingDetailsRepository;

    public FlightService(FlightRepository flightRepository, SeatsRepository seatsRepository, FlightMapper flightMapper, FlightUtil flightUtil, ReservationRepository reservationRepository, BookingDetailsRepository bookingDetailsRepository) {
        this.flightRepository = flightRepository;
        this.seatsRepository = seatsRepository;
        this.flightMapper = flightMapper;
        this.flightUtil = flightUtil;
        this.reservationRepository = reservationRepository;
        this.bookingDetailsRepository = bookingDetailsRepository;
    }

    @Transactional
    public void addNewFlight(FlightDto flightDto) {
        try {
            Flight flight = flightMapper.toFlight(flightDto);
            flightRepository.save(flight);
            flightUtil.addSeats(flightDto.getFlightNumber(),flight.getTotalSeats());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public FlightDetailsDto getAvailableFlights(FlightAvailabilityDto flightAvailabilityDto) {

        return flightUtil.getFlightFromSrcDestData(flightAvailabilityDto.getSource(),flightAvailabilityDto.getDestination(),flightAvailabilityDto.getDate().toString(),flightAvailabilityDto.getSeatType()).join();
    }


    @Transactional
    public TicketBookingResponseDto bookTickets(TicketBookingDto ticketBookingDto) {

        Seats seat=seatsRepository.findByFlightNumberAndDateAndSeatType(ticketBookingDto.getFlightNumber(), ticketBookingDto.getDate().toString(), ticketBookingDto.getSeatType());
        Flight flight=flightRepository.findByFlightNumber(ticketBookingDto.getFlightNumber());
        Long totalFare=(ticketBookingDto.getSeatType().equals(SeatType.ECONOMY)?flight.getEconomyFare():flight.getBusinessFare())*ticketBookingDto.getPassengerDtos().size();
        Set<BookingDetails> bookingDetailsLinkedHashSet=new LinkedHashSet<>();
        for(PassengerDto passengerDto:ticketBookingDto.getPassengerDtos()){
          BookingDetails bookingDetails =BookingDetails.builder()
                    .seatNumber(seat.getAvailableSeats()>0?(seat.getTotalSeats()-seat.getAvailableSeats())+1:seat.getLastWaitingSeatNumber()+1).seatType(ticketBookingDto.getSeatType())
                    .bookingDate(ticketBookingDto.getDate())
                    .ticketStatus(seat.getAvailableSeats()>0?TicketStatus.CONFIRMED:TicketStatus.WAITING)
                    .passenger(flightMapper.mapPassenger(passengerDto))
                    .build();
          seat.setAvailableSeats(seat.getAvailableSeats()-1);
          seat.setLastWaitingSeatNumber(seat.getLastWaitingSeatNumber()>0?seat.getLastWaitingSeatNumber()+1:0);
           bookingDetails= bookingDetailsRepository.save(bookingDetails);

          bookingDetailsLinkedHashSet.add(bookingDetails);
        }
      Reservation reservation=  Reservation.builder()
                .bookingDetailses(bookingDetailsLinkedHashSet)
                .flightNumber(ticketBookingDto.getFlightNumber())
                .totalAmount(totalFare)
              .reservationStatus(ReservationStatus.BOOKED).build();
        reservation=reservationRepository.save(reservation);

        return flightMapper.mapTicketBookingResponseDto(reservation,bookingDetailsLinkedHashSet);

    }
}
