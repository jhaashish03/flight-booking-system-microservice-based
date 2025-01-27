package com.ewallet.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerDto implements Serializable {
    private String name;
    private Integer age;
    private String gender;
}
