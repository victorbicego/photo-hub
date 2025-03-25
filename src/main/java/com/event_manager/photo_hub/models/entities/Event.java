package com.event_manager.photo_hub.models.entities;

import com.event_manager.photo_hub.models.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "event",
    uniqueConstraints = {@UniqueConstraint(columnNames = "qrCode")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private String name;

  @NotNull
  @Column(nullable = false)
  private LocalDateTime startDate;

  @NotNull
  @Column(nullable = false)
  private LocalDateTime endDate;

  @NotNull
  @Column(nullable = false, unique = true, length = 1028)
  private String qrCode;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Photo> photos;

  @NotNull
  @ManyToOne
  @JoinTable(
      name = "event_host",
      joinColumns = @JoinColumn(name = "event_id"),
      inverseJoinColumns = @JoinColumn(name = "host_id"))
  private Host host;

  @ManyToMany
  @JoinTable(
      name = "event_guests",
      joinColumns = @JoinColumn(name = "event_id"),
      inverseJoinColumns = @JoinColumn(name = "guest_id"))
  private List<Guest> guests;
}
