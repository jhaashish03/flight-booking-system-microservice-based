package com.microservies.flighservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_gen")
    @SequenceGenerator(name = "reservation_gen", sequenceName = "reservation_seq", initialValue = 4560)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long flightNumber;

    private Long totalAmount;



    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private OffsetDateTime lastModifiedDate;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reservation_id")
    private Set<BookingDetails> bookingDetailses = new LinkedHashSet<>();

    @Enumerated
    @Column(name = "reservationStatus")
    private ReservationStatus reservationStatus;

}
