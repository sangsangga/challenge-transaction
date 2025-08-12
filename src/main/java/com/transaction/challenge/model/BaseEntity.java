package com.transaction.challenge.model;

import java.time.ZonedDateTime;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Column;
import jakarta.persistence.PreUpdate;

@MappedSuperclass
public class BaseEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }
}
