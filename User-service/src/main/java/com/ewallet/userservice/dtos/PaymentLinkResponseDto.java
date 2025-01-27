package com.ewallet.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponseDto {
    private String paymentLink;
    private String razorpayReferenceId;
    private String referenceId;
}
