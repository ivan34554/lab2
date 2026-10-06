package com.example.lab2.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.lab2.exception.BadRequestException;
import com.example.lab2.exception.NotFoundException;
import com.example.lab2.model.AudioFile;
import com.example.lab2.model.BaseEntity;
import com.example.lab2.model.Episode;
import com.example.lab2.model.PublicationSlot;
import com.example.lab2.repository.AudioFileRepository;
import com.example.lab2.repository.EpisodeRepository;
import com.example.lab2.repository.PodcastRepository;
import com.example.lab2.repository.PublicationSlotRepository;

@Service
public class PublicationSlotService {

    private final PublicationSlotRepository slots;
    private final PodcastRepository podcasts;
    private final EpisodeRepository episodes;
    private final AudioFileRepository audioFiles;
    private final Clock clock;

    public PublicationSlotService(PublicationSlotRepository slots, PodcastRepository podcasts,
                                  EpisodeRepository episodes, AudioFileRepository audioFiles,
                                  Clock clock) {
        this.slots = slots;
        this.podcasts = podcasts;
        this.episodes = episodes;
        this.audioFiles = audioFiles;
        this.clock = clock;
    }

    public List<PublicationSlot> findAll() {
        return slots.findAll();
    }

    public PublicationSlot findById(Long id) {
        return slots.findById(id)
                .orElseThrow(() -> new NotFoundException("Publication slot " + id + " not found"));
    }

    public PublicationSlot create(Long podcastId, Long episodeId, Instant scheduledAt) {
        if (!podcasts.existsById(podcastId)) {
            throw new BadRequestException("Podcast " + podcastId + " does not exist");
        }
        Episode episode = episodes.findById(episodeId)
                .orElseThrow(() -> new BadRequestException("Episode " + episodeId + " does not exist"));
        if (!episode.getPodcastId().equals(podcastId)) {
            throw new BadRequestException("Episode " + episodeId + " does not belong to podcast " + podcastId);
        }
        requireFuture(scheduledAt);

        PublicationSlot slot = new PublicationSlot();
        slot.setPodcastId(podcastId);
        slot.setEpisodeId(episodeId);
        slot.setScheduledAt(scheduledAt);
        slot.setStatus(PublicationSlot.Status.PLANNED);
        slot.setCreatedAt(Instant.now(clock));
        return slots.save(slot);
    }

    public PublicationSlot update(Long id, Instant scheduledAt, PublicationSlot.Status status) {
        PublicationSlot slot = findById(id);
        if (slot.getStatus() == PublicationSlot.Status.PUBLISHED) {
            throw new BadRequestException("Published slot cannot be modified");
        }
        if (scheduledAt != null) {
            requireFuture(scheduledAt);
            slot.setScheduledAt(scheduledAt);
        }
        if (status != null) {
            if (status == PublicationSlot.Status.PUBLISHED) {
                publish(slot);
            }
            slot.setStatus(status);
        }
        return slots.save(slot);
    }

    public void delete(Long id) {
        findById(id);
        slots.deleteById(id);
    }

    public void deleteByEpisodeId(Long episodeId) {
        slots.findByEpisodeId(episodeId).stream()
                .map(BaseEntity::getId)
                .forEach(slots::deleteById);
    }

    private void publish(PublicationSlot slot) {
        if (slot.getScheduledAt().isAfter(Instant.now(clock))) {
            throw new BadRequestException("Slot time has not come yet");
        }
        boolean hasMain = audioFiles.findByEpisodeId(slot.getEpisodeId()).stream()
                .anyMatch(AudioFile::isMain);
        if (!hasMain) {
            throw new BadRequestException("Episode has no main audio file");
        }
        Episode episode = episodes.findById(slot.getEpisodeId())
                .orElseThrow(() -> new NotFoundException("Episode " + slot.getEpisodeId() + " not found"));
        episode.setState(Episode.State.PUBLISHED);
        episodes.save(episode);
    }

    private void requireFuture(Instant scheduledAt) {
        if (!scheduledAt.isAfter(Instant.now(clock))) {
            throw new BadRequestException("scheduledAt must be in the future");
        }
    }
}