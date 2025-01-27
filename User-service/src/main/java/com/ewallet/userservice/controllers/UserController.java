package com.ewallet.userservice.controllers;

import com.ewallet.userservice.dtos.*;
import com.ewallet.userservice.entities.Account;
import com.ewallet.userservice.entities.PaymentStatus;
import com.ewallet.userservice.services.MailService;
import com.ewallet.userservice.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Slf4j
@RequestMapping("/"+UserController.VERSION+"/users")
public class UserController {
    protected static final String VERSION="v1";
    private final UserService userService;

    public UserController(UserService userService, MailService mailService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView index() {
        return  new ModelAndView("user");
    }
    @PostMapping("/flights/checkAvailability")
    public ModelAndView getAvailableFlights(@ModelAttribute FlightAvailabilityDto flightAvailabilityDto, Model model){
        FlightDetailsDto flightDetails=userService.getAvailableFlights(flightAvailabilityDto);
        model.addAttribute("flightDetails",flightDetails);
        return  new ModelAndView("userFlightDetails");
    }

    @PostMapping("/flights/book")
    public ModelAndView bookFlight(@ModelAttribute TicketBookingDto ticketBookingDto, Model model){
        model.addAttribute("ticketBookingDto",ticketBookingDto);
        model.addAttribute("bookingRequestDto",new BookingRequestDto());
        return  new ModelAndView("bookingform");
    }
    @PostMapping("/proceedToPayment")
    public ModelAndView proceedToPayment(@ModelAttribute TicketBookingDto ticketBookingDto, @ModelAttribute BookingRequestDto bookingRequestDto, Model model){
      String[] response=userService.bookFlight(ticketBookingDto,bookingRequestDto);
      ticketBookingDto.setPassengerDtos(bookingRequestDto.getPassengers());
       ModelAndView modelAndView=new ModelAndView("checkoutPage");
        model.addAttribute("paymentUrl",response[0]);
        model.addAttribute("totalFare",response[1]);
        model.addAttribute("ticketBookingDto",ticketBookingDto);

        return modelAndView;

    }

    @GetMapping("/payment-capture")
    public ModelAndView paymentStatusCapture(Model model, @RequestParam("status") PaymentStatus paymentStatus, @RequestParam("reference_id") String referenceId ,@AuthenticationPrincipal Account user){
        if(paymentStatus==PaymentStatus.PAID){
            TicketBookingResponseDto ticketBookingResponseDto=userService.confirmBooking(referenceId);


        ModelAndView modelAndView= new ModelAndView("ticketbookingresponse");
        modelAndView.addObject("ticketBookingResponseDto",ticketBookingResponseDto);
        modelAndView.addObject("title","Ticket Booking Confirmation");
        userService.triggerAttachmentMail(user.getEmail(),modelAndView,ticketBookingResponseDto);

        return modelAndView;
    } else {
            return new ModelAndView("paymentFailed");
    }
    }

    @GetMapping("/reservation/status")
    public ModelAndView reservationStatus(@RequestParam("reservationId") String reservationId,@AuthenticationPrincipal Account user){
        ModelAndView modelAndView=new ModelAndView("ticketbookingresponse");
        TicketBookingResponseDto ticketBookingResponseDto=userService.getReservationStatus(reservationId);
        modelAndView.addObject("ticketBookingResponseDto",ticketBookingResponseDto);
        modelAndView.addObject("title","Reservation Status");
        return modelAndView;
    }

    @PostMapping("/reservation/upgrade")
    public ModelAndView upgradeReservation(@RequestParam("reservationId") String reservationId){
       TicketUpgradeRequestDto ticketUpgradeRequestDto= userService.handleUpgradeBookingRequest(reservationId);
       if(ticketUpgradeRequestDto==null){
           return new ModelAndView("upgradeNotEligible");
       } else {
           ModelAndView modelAndView=new ModelAndView("upgradeCheckoutPage");
           modelAndView.addObject("ticketUpgradeRequestDto",ticketUpgradeRequestDto);
           return modelAndView;
       }
    }

    @GetMapping("/payment-capture/upgrade-booking")
    public ModelAndView paymentStatusCaptureUpgradeRequest(Model model, @RequestParam("status") PaymentStatus paymentStatus, @RequestParam("reference_id") String referenceId ,@AuthenticationPrincipal Account user){
        if(paymentStatus==PaymentStatus.PAID){
            TicketBookingResponseDto ticketBookingResponseDto=userService.upgradeBooking(referenceId);
            ModelAndView modelAndView= new ModelAndView("ticketbookingresponse");
            modelAndView.addObject("ticketBookingResponseDto",ticketBookingResponseDto);
            modelAndView.addObject("title","Ticket Upgrade Confirmation");
            userService.triggerAttachmentMail(user.getEmail(),modelAndView,ticketBookingResponseDto);
            return modelAndView;
        } else {
            return new ModelAndView("paymentFailed");
        }
    }

    @PostMapping("/reservation/cancel")
    public ModelAndView cancelReservation(@RequestParam("reservationId") String reservationId){
        ModelAndView modelAndView=new ModelAndView("ticketCancellationConfimationPage");
        TicketBookingResponseDto ticketBookingResponseDto=userService.getReservationStatus(reservationId);
        if(ticketBookingResponseDto.getReservationStatus().equals(ReservationStatus.CANCELLED)){
          ModelAndView modelAndView1=  new ModelAndView("reservationAlreadyCancelled");
            modelAndView1.addObject("reservationId",reservationId);
            return modelAndView1;
        }
        modelAndView.addObject("ticketBookingResponseDto",ticketBookingResponseDto);
        modelAndView.addObject("title","Ticket Cancellation Confirmation");
        return modelAndView;
    }

    @PostMapping("/reservation/cancel/confirm")
    public ModelAndView confirmCancelReservation(@RequestParam("reservationId") String reservationId){
        userService.cancelReservation(reservationId);
        ModelAndView modelAndView=new ModelAndView("cancellationConfirmed");
        modelAndView.addObject("reservationId",reservationId);
       return modelAndView;
    }
}
