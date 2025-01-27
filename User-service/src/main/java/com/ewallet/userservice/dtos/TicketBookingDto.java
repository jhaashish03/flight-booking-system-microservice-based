package com.ewallet.userservice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketBookingDto implements Serializable {
    private Long flightNumber;
    private String flightName;
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime takeOffTime;
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime landingTime;
    private Long fare;
    private LocalDate date;
    private String source;
    private String destination;
    private SeatType seatType;
    @JsonDeserialize(contentAs = PassengerDto.class)
    private List<PassengerDto> passengerDtos;


}
