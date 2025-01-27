package com.ewallet.userservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GrantedAuthorityImpl implements GrantedAuthority, Serializable {
    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private  String role;

    public GrantedAuthorityImpl() {
    }

    public GrantedAuthorityImpl(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrantedAuthorityImpl that = (GrantedAuthorityImpl) o;
        return Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }
}
