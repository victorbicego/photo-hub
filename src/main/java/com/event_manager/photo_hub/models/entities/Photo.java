package com.event_manager.photo_hub.models.entities;

import com.event_manager.photo_hub.models.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
        name = "photo",
        uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends Auditable {}
