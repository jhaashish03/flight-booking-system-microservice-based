package com.microservies.flightservice.services;


import com.microservies.flightservice.dtos.*;
import com.microservies.flightservice.entities.*;
import com.microservies.flightservice.exceptions.FlightAlreadyExitsException;
import com.microservies.flightservice.mappers.FlightMapper;
import com.microservies.flightservice.repositories.BookingDetailsRepository;
import com.microservies.flightservice.repositories.FlightRepository;
import com.microservies.flightservice.repositories.ReservationRepository;
import com.microservies.flightservice.repositories.SeatsRepository;
import com.microservies.flightservice.services.util.FlightUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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
    public void addNewFlight(FlightDto flightDto) throws FlightAlreadyExitsException {
        try {
            Flight flight = flightMapper.toFlight(flightDto);
            flightRepository.save(flight);
            flightUtil.addSeats(flightDto.getFlightNumber(),flight.getTotalSeats());

        } catch (Exception e) {
            throw new FlightAlreadyExitsException("Flight Already exits with this id");
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

//            long currentSearNumberToBeAllocatedIfAvailable=0;
//            long currentSearNumberToBeAllocatedIfWaiting=0;
//            if(seat.getAvailableSeats()>0){
//                currentSearNumberToBeAllocatedIfAvailable=  ((seat.getTotalSeats()-seat.getAvailableSeats())+1);
//                seat.setAvailableSeats(seat.getAvailableSeats()-1);
//            } else {
//                currentSearNumberToBeAllocatedIfWaiting=   seat.getLastWaitingSeatNumber()+1;
//                se
//            }
          BookingDetails bookingDetails =BookingDetails.builder()
                    .seatNumber(seat.getAvailableSeats()>0?(seat.getTotalSeats()-seat.getAvailableSeats())+1:seat.getLastWaitingSeatNumber()+1).seatType(ticketBookingDto.getSeatType())
                    .ticketStatus(seat.getAvailableSeats()>0? TicketStatus.CONFIRMED:TicketStatus.WAITING)
                    .passenger(flightMapper.mapPassenger(passengerDto))
                    .build();
             if(seat.getAvailableSeats()>0) seat.setAvailableSeats(seat.getAvailableSeats()-1);

           seat.setLastWaitingSeatNumber(seat.getAvailableSeats()>0?seat.getLastWaitingSeatNumber():seat.getLastWaitingSeatNumber()+1);
           bookingDetails= bookingDetailsRepository.save(bookingDetails);

          bookingDetailsLinkedHashSet.add(bookingDetails);
        }
      Reservation reservation=  Reservation.builder()
                .bookingDetailses(bookingDetailsLinkedHashSet)
                .flightNumber(ticketBookingDto.getFlightNumber())
                .totalAmount(totalFare).seatType(ticketBookingDto.getSeatType())
              .bookingDate(ticketBookingDto.getDate())
              .reservationStatus(ReservationStatus.BOOKED).build();
        reservation=reservationRepository.save(reservation);

        return flightMapper.mapTicketBookingResponseDto(reservation,bookingDetailsLinkedHashSet,ticketBookingDto);

    }

    public TicketBookingResponseDto getReservationStatus(String reservationId) {
        Reservation reservation=reservationRepository.findById(Long.valueOf(reservationId)).get();
        Flight flight=flightRepository.findByFlightNumber(reservation.getFlightNumber());

        return flightMapper.mapTicketBookingResponseDtoForReservationStatus(reservation,flight);
    }

    public FlightDto getFlightData(String flightId) {
        return flightMapper.toFlightDto(flightRepository.findByFlightNumber(Long.valueOf(flightId)));
    }

    @Transactional
    public TicketBookingResponseDto upgradeBooking(String reservationId) {
        Reservation reservation=reservationRepository.findById(Long.valueOf(reservationId)).get();

        if(reservation.getBookingDetailses().stream().findAny().get().getSeatType().equals(SeatType.ECONOMY) && reservation.getReservationStatus().equals(ReservationStatus.BOOKED)){
        Seats seat=seatsRepository.findByFlightNumberAndDateAndSeatType(reservation.getFlightNumber(), reservation.getBookingDate().toString(), SeatType.BUSINESS);
            reservation.setTotalAmount(reservation.getBookingDetailses().size()*flightRepository.findByFlightNumber(reservation.getFlightNumber()).getBusinessFare());
            reservation.getBookingDetailses()
                    .forEach(
                            bookingDetailse->{
                                if(bookingDetailse.getTicketStatus().equals(TicketStatus.WAITING)){
                                    updateWaitingList(reservation);
                                } else {
                                    if( !updateWaitingListSeatAllocationAvailableCase(reservation,bookingDetailse.getSeatNumber())) {
                                        Seats seat1=seatsRepository.findByFlightNumberAndDateAndSeatType(reservation.getFlightNumber(), reservation.getBookingDate().toString(), SeatType.ECONOMY);

                                        if(!Objects.equals(seat1.getTotalSeats(), seat1.getAvailableSeats()))seat1.setAvailableSeats(seat1.getAvailableSeats()+1);
                                    }
                                }
                                bookingDetailse.setSeatType(SeatType.BUSINESS);
                                bookingDetailse.setSeatNumber(seat.getAvailableSeats()>0?(seat.getTotalSeats()-seat.getAvailableSeats())+1:seat.getLastWaitingSeatNumber()+1);

                                bookingDetailse.setTicketStatus(seat.getAvailableSeats()>0? TicketStatus.CONFIRMED:TicketStatus.WAITING);
                                if(seat.getAvailableSeats()>0) seat.setAvailableSeats(seat.getAvailableSeats()-1);

                                seat.setLastWaitingSeatNumber(seat.getAvailableSeats()>0?seat.getLastWaitingSeatNumber():seat.getLastWaitingSeatNumber()+1);
                            }
                    );
            reservation.setSeatType(SeatType.BUSINESS);
            return flightMapper.mapTicketBookingResponseDtoForReservationStatus(reservation,flightRepository.findByFlightNumber(reservation.getFlightNumber()));
        } else{
            throw new RuntimeException("Not eligible to upgrade booking");
        }
    }

    public FlightDetail getFlightAvailability(String flightId, SeatType seatType, LocalDate date) {
       Flight flight=flightRepository.findByFlightNumber(Long.valueOf(flightId));
      Seats seats=  seatsRepository.findByFlightNumberAndDateAndSeatType(Long.valueOf(flightId),date.toString(),seatType);
      FlightDetail flightDetail= flightMapper.mapPerFlight(flight,seats,seatType);
        if(seatType.equals(SeatType.ECONOMY)){
            flightDetail.setFare(flight.getEconomyFare());
        } else {
            flightDetail.setFare(flight.getBusinessFare());
        }
        if(seats.getLastWaitingSeatNumber()>0){
            flightDetail.setSeatStatus(SeatStatus.WAITING);
            flightDetail.setSeats(seats.getLastWaitingSeatNumber());
        } else {
            flightDetail.setSeatStatus(SeatStatus.AVAILABLE);
            flightDetail.setSeats(seats.getAvailableSeats());
        }
    return flightDetail;
    }

    @Transactional
    public Void cancelBooking(String reservationId) {
        Reservation reservation=reservationRepository.findById(Long.valueOf(reservationId)).get();
        Seats seats=seatsRepository.findByFlightNumberAndDateAndSeatType(reservation.getFlightNumber(), String.valueOf(reservation.getBookingDate()),reservation.getSeatType());
        for (BookingDetails bookingDetails : reservation.getBookingDetailses()) {
           if(bookingDetails.getTicketStatus().equals(TicketStatus.CONFIRMED)){
               bookingDetails.setTicketStatus(TicketStatus.CANCELLED);

              if( !updateWaitingListSeatAllocationAvailableCase(reservation,bookingDetails.getSeatNumber())) seats.setAvailableSeats(seats.getAvailableSeats()+1);

           } else if (bookingDetails.getTicketStatus().equals(TicketStatus.WAITING)){
               bookingDetails.setTicketStatus(TicketStatus.CANCELLED);
               updateWaitingList(reservation);
           }
        }
        reservation.setReservationStatus(ReservationStatus.CANCELLED);


        return null;
    }

    @Transactional
    protected boolean updateWaitingListSeatAllocationAvailableCase(Reservation reservation,long seatNumber) {
       List<Reservation> reservations=  reservationRepository.updateWaitingListGreaterThan(
                reservation.getFlightNumber(),reservation.getBookingDate(),reservation.getSeatType(), ReservationStatus.BOOKED,reservation.getCreatedDate());
        for(Reservation res:reservations){
            for(BookingDetails bookingDetails : res.getBookingDetailses()) {
                if(bookingDetails.getTicketStatus().equals(TicketStatus.WAITING)){
                    bookingDetails.setTicketStatus(TicketStatus.CONFIRMED);
                    bookingDetails.setSeatNumber(seatNumber);
                    updateWaitingList(reservation);
                    return true;
                }
            }
            }
        return false;

    }

    @Transactional
    protected void updateWaitingList(Reservation reservation) {
        List<Reservation> reservations=reservationRepository.updateWaitingListGreaterThan(reservation.getFlightNumber(),reservation.getBookingDate(),reservation.getSeatType(), ReservationStatus.BOOKED,reservation.getCreatedDate());
        for(Reservation res:reservations){
            for(BookingDetails bookingDetails : res.getBookingDetailses()) {
                if(bookingDetails.getTicketStatus().equals(TicketStatus.WAITING) )bookingDetails.setSeatNumber(bookingDetails.getSeatNumber()-1);
                }
            }
        }


    public List<ReservationDto> getReservationReport(String date, String destination) {

        List<Flight> flights=flightRepository.findByDestinationLikeOrderByLandingTimeAsc(destination);

        List<ReservationDto> reservationDtos=new ArrayList<>();
        for(Flight flight:flights){
            List<Reservation> reservations=reservationRepository.findByFlightNumberAndBookingDateOrderByCreatedDateAsc(flight.getFlightNumber(),LocalDate.parse(date));
            for(Reservation reservation:reservations){
                reservationDtos.add(ReservationDto.builder()
                        .bookingDate(reservation.getBookingDate())
                        .reservationId(reservation.getId())
                        .passengersCount((long) reservation.getBookingDetailses().size())
                        .flightNumber(reservation.getFlightNumber())
                        .reservationStatus(reservation.getReservationStatus())
                        .totalAmount(reservation.getTotalAmount())
                        .seatType(reservation.getSeatType()).build());
            }
        }
        return reservationDtos;
    }
}
