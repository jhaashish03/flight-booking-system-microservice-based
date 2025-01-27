package com.microservies.flighservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "seats_table")
public class Seats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;



    private Long totalSeats;
    private Long availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    private Long flightNumber;
    private Long lastWaitingSeatNumber;



//    @Enumerated(EnumType.STRING)
//    @Column(name = "flight_running_status", nullable = false)
//    private FlightRunningStatus flightRunningStatus;

    private String date;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private OffsetDateTime lastModifiedDate;

}
