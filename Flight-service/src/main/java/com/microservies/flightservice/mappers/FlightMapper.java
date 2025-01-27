package com.microservies.flightservice.mappers;

import com.microservies.flightservice.dtos.*;
import com.microservies.flightservice.entities.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface FlightMapper {





    @Mapping(target = "businessFare", source = "flightDto.businessFare")
    @Mapping(target = "economyFare", source = "economyFare")
    @Mapping(target = "lastModifiedDate",ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Flight toFlight(FlightDto flightDto);

    @InheritInverseConfiguration
    FlightDto toFlightDto(Flight flight);

    @Mapping(target = "seats",source = "seats")
    @Mapping(target = "flightName",source = "flight.flightName")
    @Mapping(target = "fare",ignore = true)
    @Mapping(target = "flightNumber",source = "flight.flightNumber")
    @Mapping(target = "landingTime",source = "flight.landingTime")
    @Mapping(target = "takeOffTime",source = "flight.takeOffTime")
    FlightDetail mapFlight(Flight flight, Long seats, SeatType seatType);

    @AfterMapping
    default void afterMapping(@MappingTarget FlightDetail flightDetail, Flight flight, Long seats, SeatType seatType) {
        if(seatType.equals(SeatType.ECONOMY)){
            flightDetail.setFare(flight.getEconomyFare());
        } else {
            flightDetail.setFare(flight.getBusinessFare());
        }
    }


    Set<Passenger> mapPassengersDtoList(List<PassengerDto> passengerDtos);

    List<PassengerDto> mapPassengersList(List<Passenger> passengers);

    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    Passenger mapPassenger(PassengerDto passengerDto);

    @InheritInverseConfiguration
    PassengerDto mapPassenger(Passenger passengerDto);


   @Mapping(target = "totalFare", source = "reservation.totalAmount")
   @Mapping(target = "takeOffTime", source = "ticketBookingDto.takeOffTime")
   @Mapping(target = "source", source = "ticketBookingDto.source")
   @Mapping(target = "landingTime", source = "ticketBookingDto.landingTime")
   @Mapping(target = "flightName", source = "ticketBookingDto.flightName")
   @Mapping(target = "destination", source = "ticketBookingDto.destination")
   @Mapping(target = "date", source = "ticketBookingDto.date")
   @Mapping(target = "reservationStatus", source = "reservation.reservationStatus")
   @Mapping(target = "reservationId", source = "reservation.id")
   @Mapping(target = "bookingDetailPassengerWises", source = "bookingDetails")
   @Mapping(target = "flightNumber", source = "ticketBookingDto.flightNumber")
   TicketBookingResponseDto mapTicketBookingResponseDto(Reservation reservation, Set<BookingDetails> bookingDetails, TicketBookingDto ticketBookingDto);


   TicketBookingResponseDto.BookingDetailPassengerWise mapBookingDetailPassengerWise(BookingDetails bookingDetails);

   List<TicketBookingResponseDto.BookingDetailPassengerWise> mapBookingDetailPassengerWiseList(Set<BookingDetails> bookingDetails);


   @Mapping(target = "totalFare", source = "reservation.totalAmount")
   @Mapping(target = "takeOffTime", source = "flight.takeOffTime")
   @Mapping(target = "source", source = "flight.source")
   @Mapping(target = "reservationId", source = "reservation.id")
   @Mapping(target = "landingTime", source = "flight.landingTime")
   @Mapping(target = "flightName", source = "flight.flightName")
   @Mapping(target = "destination", source = "flight.destination")
   @Mapping(target = "date", source = "reservation.bookingDate")
   @Mapping(target = "bookingDetailPassengerWises", source = "reservation.bookingDetailses")
   @Mapping(target = "flightNumber", source = "reservation.flightNumber")
   TicketBookingResponseDto mapTicketBookingResponseDtoForReservationStatus(Reservation reservation,Flight flight);

    @Mapping(target = "seatStatus", ignore = true)
    @Mapping(target = "fare", ignore = true)
    @Mapping(target = "flightNumber", source = "flight.flightNumber")
    @Mapping(target = "seats", ignore = true)
    FlightDetail mapPerFlight(Flight flight, Seats seats,SeatType seatType);

//    @AfterMapping
//    default void afterMapping(@MappingTarget FlightDetail flightDetail, Flight flight, Seats seats, SeatType seatType) {
//
//        if(seatType.equals(SeatType.ECONOMY)){
//            flightDetail.setFare(flight.getEconomyFare());
//        } else {
//            flightDetail.setFare(flight.getBusinessFare());
//        }
//        if(seats.getLastWaitingSeatNumber()>0){
//            flightDetail.setSeatStatus(SeatStatus.WAITING);
//            flightDetail.setSeats(seats.getLastWaitingSeatNumber());
//        } else {
//            flightDetail.setSeatStatus(SeatStatus.AVAILABLE);
//            flightDetail.setSeats(seats.getAvailableSeats());
//        }
//    }

}
