package com.example.lab2.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.lab2.exception.BadRequestException;
import com.example.lab2.exception.NotFoundException;
import com.example.lab2.model.BaseEntity;
import com.example.lab2.model.Episode;
import com.example.lab2.repository.EpisodeRepository;
import com.example.lab2.repository.PodcastRepository;

@Service
public class EpisodeService {

    private final EpisodeRepository episodes;
    private final PodcastRepository podcasts;
    private final AudioFileService audioFiles;
    private final Clock clock;

    public EpisodeService(EpisodeRepository episodes, PodcastRepository podcasts,
                          AudioFileService audioFiles, Clock clock) {
        this.episodes = episodes;
        this.podcasts = podcasts;
        this.audioFiles = audioFiles;
        this.clock = clock;
    }

    public List<Episode> findAll() {
        return episodes.findAll();
    }

    public Episode findById(Long id) {
        return episodes.findById(id)
                .orElseThrow(() -> new NotFoundException("Episode " + id + " not found"));
    }

    public Episode create(Long podcastId, String title, int durationSeconds) {
        if (!podcasts.existsById(podcastId)) {
            throw new BadRequestException("Podcast " + podcastId + " does not exist");
        }
        Episode episode = new Episode();
        episode.setPodcastId(podcastId);
        episode.setTitle(title);
        episode.setDurationSeconds(durationSeconds);
        episode.setState(Episode.State.DRAFT);
        episode.setCreatedAt(Instant.now(clock));
        return episodes.save(episode);
    }

    public Episode update(Long id, String title, Integer durationSeconds, Episode.State state) {
        Episode episode = findById(id);
        if (title != null) episode.setTitle(title);
        if (durationSeconds != null) episode.setDurationSeconds(durationSeconds);
        if (state != null) episode.setState(state);
        return episodes.save(episode);
    }

    public void delete(Long id) {
        findById(id);
        audioFiles.deleteByEpisodeId(id);
        episodes.deleteById(id);
    }

    public void deleteByPodcastId(Long podcastId) {
        episodes.findByPodcastId(podcastId).stream()
                .map(BaseEntity::getId)
                .forEach(this::delete);
    }
}