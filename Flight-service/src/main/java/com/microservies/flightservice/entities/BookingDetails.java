package com.microservies.flightservice.entities;

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
public class BookingDetails {
    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false)
    private TicketStatus ticketStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    private Long seatNumber;



    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private OffsetDateTime lastModifiedDate;

}
