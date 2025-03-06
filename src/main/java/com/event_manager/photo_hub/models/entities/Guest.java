package com.event_manager.photo_hub.models.entities;

import com.event_manager.photo_hub.models.Auditable;
import com.event_manager.photo_hub.models.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "guest",
    uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guest extends Auditable implements UserDetails, Serializable {

  private static final Role ROLE = Role.GUEST;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Email
  @Column(nullable = false, unique = true)
  private String username;

  @NotNull
  @Size(min = 8)
  @Column(nullable = false)
  private String password;

  @NotNull
  @Column(nullable = false)
  private Boolean enabled;

  @Size(min = 1, max = 50)
  @NotNull
  @Column(nullable = false)
  private String firstName;

  @Size(min = 1, max = 50)
  @NotNull
  @Column(nullable = false)
  private String lastName;

  @ManyToMany(mappedBy = "guests")
  private List<Event> events;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(ROLE.name()));
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
}
