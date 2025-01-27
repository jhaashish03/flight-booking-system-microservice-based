package com.ewallet.userservice.services;

import com.ewallet.userservice.dtos.*;
import com.ewallet.userservice.entities.Account;
import com.ewallet.userservice.services.client.FlightServiceOpenFeignClient;
import com.ewallet.userservice.services.client.PaymentServiceOpenFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {

    private final FlightServiceOpenFeignClient flightServiceOpenFeignClient;
    private final PaymentServiceOpenFeignClient paymentServiceOpenFeignClient;
    private final RedisTemplate<String,Object> redisTemplate;
    private final VersionedAsyncRestClient versionedAsyncRestClient;
    private final ObjectMapper objectMapper;
    private final MailService mailService;

    public UserService(FlightServiceOpenFeignClient flightServiceOpenFeignClient, PaymentServiceOpenFeignClient paymentServiceOpenFeignClient, RedisTemplate<String, Object> redisTemplate, VersionedAsyncRestClient versionedAsyncRestClient, ObjectMapper objectMapper, MailService mailService) {
        this.flightServiceOpenFeignClient = flightServiceOpenFeignClient;
        this.paymentServiceOpenFeignClient = paymentServiceOpenFeignClient;
        this.redisTemplate = redisTemplate;
        this.versionedAsyncRestClient = versionedAsyncRestClient;
        this.objectMapper = objectMapper;
        this.mailService = mailService;
    }


    public FlightDetailsDto getAvailableFlights(FlightAvailabilityDto flightAvailabilityDto){
        return   flightServiceOpenFeignClient.getAvailability(flightAvailabilityDto).getBody();
    }



    @Transactional
    public String[] bookFlight(TicketBookingDto ticketBookingDto, BookingRequestDto bookingRequestDto) {
      Account account= (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ticketBookingDto.setPassengerDtos(bookingRequestDto.getPassengers());
        String referenceId= UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(referenceId,ticketBookingDto, Duration.ofMinutes(5));
        PaymentLinkResponseDto paymentLinkResponseDto = versionedAsyncRestClient.post("http://localhost:8082/payments/payment", PaymentRequestDto.builder()
                        .email(account.getEmail())
                        .name(account.getFirstName())
                        .callbackUrl("http://localhost:6050/air-india/users/v1/users/payment-capture")
                        .amount((double) (ticketBookingDto.getFare()*bookingRequestDto.getPassengers().size()))
                        .referenceId(referenceId).contactNumber(null).description("Ticket Payment").build(),
                PaymentLinkResponseDto.class, new HttpHeaders()).join().getBody();

          /*  paymentLinkResponseDto = paymentServiceOpenFeignClient.payment(PaymentRequestDto.builder()
                     .email(account.getEmail())
                     .name(account.getFirstName())
                     .callbackUrl("http://localhost:7072/air-india/users/v1/users/payment-capture")
                            .amount((double) (ticketBookingDto.getFare()*bookingRequestDto.getPassengers().size()))
                     .referenceId(referenceId).contactNumber(null).description("Ticket Payment").build()).getBody();*/


        assert paymentLinkResponseDto != null;
        return new String[]{paymentLinkResponseDto.getPaymentLink(), String.valueOf((ticketBookingDto.getFare()*bookingRequestDto.getPassengers().size()))};
    }

    public TicketBookingResponseDto confirmBooking(String referenceId) {
        TicketBookingDto bookingDto= (TicketBookingDto) redisTemplate.opsForValue().get(referenceId);

        return flightServiceOpenFeignClient.bookTickets(bookingDto).getBody();

     /*   CollectionType listType =
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, PassengerDto.class);
        objectMapper.readValue(objectMapper.writeValueAsString(object), listType);*/


    }

    public TicketBookingResponseDto getReservationStatus(String reservationId){
        return flightServiceOpenFeignClient.getReservationStatus(reservationId).getBody();
    }

    @Transactional
    public TicketUpgradeRequestDto handleUpgradeBookingRequest(String reservationId){

        TicketBookingResponseDto ticketBookingResponseDto=getReservationStatus(reservationId);
        if(ticketBookingResponseDto.getBookingDetailPassengerWises().get(0).getSeatType().equals(SeatType.ECONOMY) && ticketBookingResponseDto.getReservationStatus().equals(ReservationStatus.BOOKED)){
        FlightDto flightDto=flightServiceOpenFeignClient.getFlight(ticketBookingResponseDto.getFlightNumber().toString()).getBody();

            assert flightDto != null;
            long extraAmount= (flightDto.getBusinessFare()-flightDto.getEconomyFare())*ticketBookingResponseDto.getBookingDetailPassengerWises().size();
            Account account= (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String referenceId= UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(account.getEmail(),referenceId, Duration.ofMinutes(5));
            redisTemplate.opsForValue().set(referenceId,reservationId, Duration.ofMinutes(5));
            PaymentLinkResponseDto paymentLinkResponseDto = versionedAsyncRestClient.post("http://localhost:8082/payments/payment", PaymentRequestDto.builder()
                            .email(account.getEmail())
                            .name(account.getFirstName())
                            .callbackUrl("http://localhost:6050/air-india/users/v1/users/payment-capture/upgrade-booking")
                            .amount((double) extraAmount)
                            .referenceId(referenceId).contactNumber(null).description("Ticket Upgrade Payment").build(),
                    PaymentLinkResponseDto.class, new HttpHeaders()).join().getBody();
            assert paymentLinkResponseDto != null;
           FlightDetail flightDetail= flightServiceOpenFeignClient.getAvailabilityFlight(flightDto.getFlightNumber().toString(), SeatType.BUSINESS, ticketBookingResponseDto.getDate()).getBody();
            assert flightDetail != null;
           return TicketUpgradeRequestDto.builder()
                    .amountToBePaid(extraAmount)
                    .date(ticketBookingResponseDto.getDate())
                    .bookingDetailPassengerWises(ticketBookingResponseDto.getBookingDetailPassengerWises())
                    .flightName(ticketBookingResponseDto.getFlightName())
                    .flightNumber(ticketBookingResponseDto.getFlightNumber())
                    .landingTime(ticketBookingResponseDto.getLandingTime())
                    .paidAmount(ticketBookingResponseDto.getTotalFare())
                    .seatStatus(flightDetail.getSeatStatus())
                    .takeOffTime(flightDto.getTakeOffTime())
                    .source(flightDto.getSource())
                    .destination(flightDto.getDestination())
                    .paymentUrl(paymentLinkResponseDto.getPaymentLink())
                    .reservationStatus(ticketBookingResponseDto.getReservationStatus())
                    .seatsAvailable(flightDetail.getSeats())
                    .reservationId(reservationId).build();
        } else {
           return null;
        }
    }

    public TicketBookingResponseDto upgradeBooking(String referenceId) {
        Account account= (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       if(Objects.equals(redisTemplate.opsForValue().get(account.getEmail()), referenceId)){
         String reservationId= (String) redisTemplate.opsForValue().get(referenceId);
         return flightServiceOpenFeignClient.upgradeBooking(reservationId).getBody();
       } else {
         throw new RuntimeException();
       }

    }

    public void cancelReservation(String reservationId){
        flightServiceOpenFeignClient.cancelReservation(reservationId);
    }

    @Async
    public void triggerAttachmentMail(String email, ModelAndView modelAndView, TicketBookingResponseDto ticketBookingResponseDto) {
        mailService.triggerTicketConfirmationMail(email,modelAndView,ticketBookingResponseDto);
    }


}
