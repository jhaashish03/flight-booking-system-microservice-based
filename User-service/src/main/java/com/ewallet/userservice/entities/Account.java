package com.ewallet.userservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor@ToString
@Table(name = "user_table")
public class Account implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String userName;


    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;



    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "authority")
    @CollectionTable(name = "user_table_authorities", joinColumns = @JoinColumn(name = "owner_id"))
    private Set<GrantedAuthorityImpl> authority = new LinkedHashSet<>();




    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_date")
    private OffsetDateTime createdDate;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private OffsetDateTime lastModifiedDate;



    @Override
    public String getPassword() {
        return this.password;
    }


    @Override
    public Collection<GrantedAuthorityImpl> getAuthorities() {

        return this.authority;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
