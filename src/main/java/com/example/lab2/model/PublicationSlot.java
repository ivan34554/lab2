package com.example.lab2.model;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicationSlot extends BaseEntity {
    public enum Status { PLANNED, PUBLISHED, CANCELLED }

    private Long podcastId;
    private Long episodeId;
    private Instant scheduledAt;
    private Status status;
}