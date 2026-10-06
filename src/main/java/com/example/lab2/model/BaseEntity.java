package com.example.lab2.model;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEntity {
    private Long id;
    private Instant createdAt;
}