package com.ewallet.userservice.services.client;

import com.ewallet.userservice.dtos.PaymentLinkResponseDto;
import com.ewallet.userservice.dtos.PaymentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceOpenFeignClient {

    @PostMapping("/payments/payment")
    public ResponseEntity<PaymentLinkResponseDto> payment(@RequestBody PaymentRequestDto paymentRequestDto);
}
