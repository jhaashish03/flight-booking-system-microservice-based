package com.ewallet.userservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @NotNull
    private String to;
    @NotNull
    private String subject;
    @NotNull
    private String body;
    private String message;
}
