package com.microservies.flighservice.mappers;

import com.microservies.flighservice.dtos.*;
import com.microservies.flighservice.entities.*;
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
    default void afterMapping(@MappingTarget FlightDetail flightDetail, Flight flight,Long seats,SeatType seatType) {
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


   @Mapping(target = "reservationStatus", source = "reservation.reservationStatus")
   @Mapping(target = "reservationId", source = "reservation.id")
   @Mapping(target = "bookingDetailPassengerWises", source = "bookingDetails")
   TicketBookingResponseDto mapTicketBookingResponseDto(Reservation reservation, Set<BookingDetails> bookingDetails);


   TicketBookingResponseDto.BookingDetailPassengerWise mapBookingDetailPassengerWise(BookingDetails bookingDetails);

   List<TicketBookingResponseDto.BookingDetailPassengerWise> mapBookingDetailPassengerWiseList(Set<BookingDetails> bookingDetails);
}
