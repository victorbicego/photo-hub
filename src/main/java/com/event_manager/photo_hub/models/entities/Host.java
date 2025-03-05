package com.event_manager.photo_hub.models.entities;

import com.event_manager.photo_hub.models.Auditable;
import com.event_manager.photo_hub.models.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "host",
        uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host extends Auditable implements UserDetails, Serializable {

    private String username;
    private String password;
    private final Role role = Role.HOST;
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
