package com.example.lab2.dto;

import java.time.Instant;
import com.example.lab2.model.PublicationSlot;
import jakarta.validation.constraints.NotNull;

public final class PublicationSlotDto {
    private PublicationSlotDto() {}

    public record CreateRequest(
            @NotNull Long podcastId,
            @NotNull Long episodeId,
            @NotNull Instant scheduledAt) {}

    public record UpdateRequest(
            Instant scheduledAt,
            PublicationSlot.Status status) {}

    public record Response(Long id, Long podcastId, Long episodeId, Instant scheduledAt,
                           PublicationSlot.Status status, Instant createdAt) {}
}