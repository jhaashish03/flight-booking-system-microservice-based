package com.microservies.paymentservice.controllers;

import com.microservies.paymentservice.dtos.PaymentLinkResponseDto;
import com.microservies.paymentservice.dtos.PaymentRequestDto;
import com.microservies.paymentservice.services.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentLinkResponseDto> payment(@RequestBody PaymentRequestDto paymentRequestDto) {

        return ResponseEntity.accepted().body(paymentService.createPaymentLink(paymentRequestDto));
    }


    //Callback url of Razorapy on payment completion
    //http://localhost:8082/?razorpay_payment_id=pay_NrT8tm87B2ibka&razorpay_payment_link_id=plink_NrT6cskccWea74&razorpay_payment_link_reference_id=24dfe62e-52ed-4088-aa70-eb38facdc2d3&razorpay_payment_link_status=paid&razorpay_signature=604741719840e66d608929c5e2bdc32ec7137db18f6079d5c40714a8b5edf734

    @GetMapping("/payment-status-capture")
    public ResponseEntity<String> verifyPaymentStatus(@RequestParam("razorpay_payment_id") String razorpay_payment_id, @RequestParam("razorpay_payment_link_id") String razorpay_payment_link_id, @RequestParam("razorpay_payment_link_reference_id") String razorpay_payment_link_reference_id, @RequestParam("razorpay_payment_link_status") String razorpay_payment_link_status, @RequestParam("razorpay_signature") String razorpay_signature) throws RazorpayException {
      String url=  paymentService.verifyPayment(razorpay_payment_id, razorpay_payment_link_id, razorpay_payment_link_reference_id, razorpay_payment_link_status, razorpay_signature);
        HttpHeaders headers=new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers,HttpStatus.PERMANENT_REDIRECT);
    }
}
