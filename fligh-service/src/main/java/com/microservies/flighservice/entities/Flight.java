package com.microservies.flighservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor@ToString
@Table(name = "flght_table")
public class Flight {

    @Column(name = "flight_number", nullable = false, unique = true)
    private Long flightNumber;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "flight_name", nullable = false)
    private String flightName;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "landing_time", nullable = false)
    private LocalTime landingTime;

    @Column(name = "enonomy_fare", nullable = false)
    private Long economyFare;

    @Column(name = "totalSeats", nullable = false)
    private Long totalSeats;

    @Column(name = "take_off_time", nullable = false)
    private LocalTime takeOffTime;

    @Column(name = "business_fare", nullable = false)
    private Long businessFare;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private OffsetDateTime lastModifiedDate;
}
