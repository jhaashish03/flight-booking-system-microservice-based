package com.ewallet.userservice.controllers;

import com.ewallet.userservice.dtos.*;
import com.ewallet.userservice.exceptions.DuplicateEntryException;
import com.ewallet.userservice.services.AccountService;
import com.ewallet.userservice.services.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/"+AdminController.VERSION+"/admins")
public class AdminController {
    protected static final String VERSION="v1";
    private final AccountService accountService;
    private final AdminService adminService;

    public AdminController(AccountService accountService, AdminService adminService) {
        this.accountService = accountService;
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("accountCreationDto", new AccountCreationDto());
        model.addAttribute("flightCreationDto", new FlightCreationDto());
        model.addAttribute("flightAvailabilityDto", new FlightAvailabilityDto());
        return "admin";
    }

    @PostMapping("/admin")
    public String addAdmin(@ModelAttribute AccountCreationDto accountCreationDto,Model model) {
    accountService.createAdmin(accountCreationDto);

        model.addAttribute("adminSuccessPageDto", AdminSuccessPageDto.builder()
                .subject("Admin Created Successfully!").subSubject("The new admin account has been created. ").build());

    return "admin-success";
    }

    @PostMapping("/flights/flight")
    public String addFlight(@ModelAttribute FlightCreationDto flightCreationDto,Model model) throws DuplicateEntryException {
        adminService.addFlight(flightCreationDto);
        model.addAttribute("adminSuccessPageDto", AdminSuccessPageDto.builder()
                .subject("Flight added!").subSubject("New flight has been added successfully. ").build());

        return "admin-success";
    }

    @PostMapping("/flights/checkAvailability")
    public String getAvailableFlights(@ModelAttribute FlightAvailabilityDto flightAvailabilityDto,Model model){
        FlightDetailsDto flightDetails=adminService.getAvailableFlights(flightAvailabilityDto);
        model.addAttribute("flightDetails",flightDetails);
        return "flightDetails";
    }

    @PostMapping("/generate-reservation-report")
    public ResponseEntity<StreamingResponseBody> generateProductsCSV(@RequestParam("date") LocalDate bookingDate, @RequestParam("destination") String destination, final HttpServletResponse httpServletResponse) throws IOException {
        String filename = "reservationsReport"+bookingDate.toString()+"For"+destination+".csv";
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+filename+"\"").
                contentType(MediaType.parseMediaType("application/csv")).body(stream->stream.write(adminService.generateProductsCSV(bookingDate,destination).toByteArray()));

    }
}
