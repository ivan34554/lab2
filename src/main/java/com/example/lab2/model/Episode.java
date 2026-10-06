package com.example.lab2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Episode extends BaseEntity {
    public enum State { DRAFT, SCHEDULED, PUBLISHED }

    private Long podcastId;
    private String title;
    private int durationSeconds;
    private State state;
}