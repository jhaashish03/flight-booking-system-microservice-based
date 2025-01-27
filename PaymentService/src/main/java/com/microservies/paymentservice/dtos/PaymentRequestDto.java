package com.microservies.paymentservice.dtos;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Data
@Builder@NoArgsConstructor@AllArgsConstructor
public class PaymentRequestDto implements Serializable {

    private Double amount;
    private String referenceId;
    private String description;
    private String name;
    private String contactNumber;
    private String email;
    private String callbackUrl;

}
