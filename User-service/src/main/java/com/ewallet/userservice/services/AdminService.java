package com.ewallet.userservice.services;

import com.ewallet.userservice.dtos.FlightAvailabilityDto;
import com.ewallet.userservice.dtos.FlightCreationDto;
import com.ewallet.userservice.dtos.FlightDetailsDto;
import com.ewallet.userservice.dtos.ReservationDto;
import com.ewallet.userservice.entities.ReservationsDataCSVFormat;
import com.ewallet.userservice.exceptions.DuplicateEntryException;
import com.ewallet.userservice.services.client.FlightServiceOpenFeignClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {
    private final FlightServiceOpenFeignClient flightServiceOpenFeignClient;


    public AdminService(FlightServiceOpenFeignClient flightServiceOpenFeignClient) {
        this.flightServiceOpenFeignClient = flightServiceOpenFeignClient;
    }
    public void addFlight(FlightCreationDto flightCreationDto) throws DuplicateEntryException {

        try {
            flightServiceOpenFeignClient.addFlight(flightCreationDto);
        } catch (Exception e) {
            throw new DuplicateEntryException("Duplicate Extry");
        }

    }
    public FlightDetailsDto getAvailableFlights(FlightAvailabilityDto  flightAvailabilityDto){
      return   flightServiceOpenFeignClient.getAvailability(flightAvailabilityDto).getBody();
    }

    public ByteArrayOutputStream generateProductsCSV(LocalDate bookingDate, String destination) throws IOException {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader(ReservationsDataCSVFormat.class).setAutoFlush(true).build();
        ByteArrayOutputStream byteArrayOutputStreamerBot=new ByteArrayOutputStream();
        try(CSVPrinter csvPrinter=new CSVPrinter(new PrintWriter(byteArrayOutputStreamerBot), csvFormat)){
            List<ReservationDto> reservationDtos=flightServiceOpenFeignClient.getReservationReport(bookingDate.toString(),destination).getBody();
            assert reservationDtos != null;
            reservationDtos.forEach(reservationDto -> {
                try {
                    csvPrinter.printRecord(reservationDto.getBookingDate().toString(),destination ,reservationDto.getReservationId(),reservationDto.getFlightNumber(),
                            reservationDto.getTotalAmount(),reservationDto.getSeatType(),reservationDto.getPassengersCount(),reservationDto.getReservationStatus().name());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
            csvPrinter.flush();
            return byteArrayOutputStreamerBot;
        }

    }
}
