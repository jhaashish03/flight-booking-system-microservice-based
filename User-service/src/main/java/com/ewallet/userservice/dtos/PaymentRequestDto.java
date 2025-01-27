package com.ewallet.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder@NoArgsConstructor@AllArgsConstructor
public class PaymentRequestDto {

    private Double amount;
    private String referenceId;
    private String description;
    private String name;
    private String contactNumber;
    private String email;
    private String callbackUrl;

}
