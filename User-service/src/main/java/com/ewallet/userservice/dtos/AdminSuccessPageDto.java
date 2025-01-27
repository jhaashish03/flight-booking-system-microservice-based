package com.ewallet.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor@Builder
public class AdminSuccessPageDto {
    private String subject;
    private String subSubject;
}
