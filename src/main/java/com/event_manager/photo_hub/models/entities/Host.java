package com.event_manager.photo_hub.models.entities;

import com.event_manager.photo_hub.models.Auditable;
import com.event_manager.photo_hub.models.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
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
    name = "host",
    uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host extends Auditable implements UserDetails, Serializable {

  private static final Role ROLE = Role.HOST;

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

  @OneToMany(mappedBy = "host")
  private List<Event> events;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(ROLE.name()));
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
}
