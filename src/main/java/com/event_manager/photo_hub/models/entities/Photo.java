package com.event_manager.photo_hub.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.event_manager.photo_hub.models.Auditable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "photo",
        uniqueConstraints = {@UniqueConstraint(columnNames = "photoUrl")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull
    @Column(nullable = false, unique = true)
    private String photoUrl;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime uploadDate;

    @NotNull
    @Column(nullable = false)
    private String contentType;
}
