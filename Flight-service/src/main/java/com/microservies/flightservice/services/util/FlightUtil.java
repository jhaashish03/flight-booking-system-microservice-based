package com.microservies.flightservice.services.util;

import com.microservies.flightservice.dtos.FlightDetail;
import com.microservies.flightservice.dtos.FlightDetailsDto;
import com.microservies.flightservice.entities.Flight;
import com.microservies.flightservice.entities.SeatStatus;
import com.microservies.flightservice.entities.SeatType;
import com.microservies.flightservice.entities.Seats;
import com.microservies.flightservice.mappers.FlightMapper;
import com.microservies.flightservice.repositories.FlightRepository;
import com.microservies.flightservice.repositories.SeatsRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class FlightUtil {

    private final SeatsRepository seatsRepository;
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;


    public FlightUtil(SeatsRepository seatsRepository, FlightRepository flightRepository, FlightMapper flightMapper) {
        this.seatsRepository = seatsRepository;
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
    }

    @Async
    @Transactional
    public void addSeats(Long flightNumber, Long seats){
        List<Seats> seatsList = new ArrayList<Seats>();
        for (int i = 0; i <30 ; i++) {
            seatsList.add(Seats.builder().
                    flightNumber(flightNumber)
                    .totalSeats(seats)
                            .availableSeats(seats)
                    .seatType(SeatType.ECONOMY)
                    .date(LocalDate.now().plusDays(i).toString()).build());
            seatsList.add(Seats.builder().
                    flightNumber(flightNumber)
                    .totalSeats(seats)
                    .availableSeats(seats)
                            .seatType(SeatType.BUSINESS)
                    .date(LocalDate.now().plusDays(i).toString()).build());
        }
        seatsRepository.saveAll(seatsList);
    }

    @Async
    public CompletableFuture<FlightDetailsDto> getFlightFromSrcDestData(String src, String dest, String date, SeatType seatType){
        List<Flight> flightDetailList= flightRepository.findBySrcAndDest(src,dest);
        List<FlightDetail> flightDetails=new ArrayList<>();
        for(Flight flight:flightDetailList){
          Seats seats= seatsRepository.findByFlightNumberAndDateAndSeatType(flight.getFlightNumber(),date,seatType);
           if(seats!=null){
             FlightDetail flightDetail=  flightMapper.mapFlight(flight, seats.getAvailableSeats(),seatType);
               flightDetail.setFare(seatType.equals(SeatType.BUSINESS)?flight.getBusinessFare():flight.getEconomyFare());
                flightDetail.setSeats(seats.getAvailableSeats()>0?seats.getAvailableSeats():seats.getLastWaitingSeatNumber());
                flightDetail.setSeatStatus(seats.getAvailableSeats()>0? SeatStatus.AVAILABLE:SeatStatus.WAITING);
               flightDetails.add(flightDetail);

           }

        }
        return CompletableFuture.completedFuture(FlightDetailsDto.builder().flightDetailList(flightDetails).source(src).destination(dest).date(LocalDate.parse(date)).seatType(seatType).build());
    }
}
