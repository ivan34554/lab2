package com.example.lab2.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.lab2.exception.BadRequestException;
import com.example.lab2.exception.NotFoundException;
import com.example.lab2.model.BaseEntity;
import com.example.lab2.model.Podcast;
import com.example.lab2.repository.PodcastRepository;
import com.example.lab2.repository.UserRepository;

@Service
public class PodcastService {

    private final PodcastRepository podcasts;
    private final UserRepository users;
    private final EpisodeService episodes;
    private final Clock clock;

    public PodcastService(PodcastRepository podcasts, UserRepository users,
                          EpisodeService episodes, Clock clock) {
        this.podcasts = podcasts;
        this.users = users;
        this.episodes = episodes;
        this.clock = clock;
    }

    public List<Podcast> findAll() {
        return podcasts.findAll();
    }

    public Podcast findById(Long id) {
        return podcasts.findById(id)
                .orElseThrow(() -> new NotFoundException("Podcast " + id + " not found"));
    }

    public Podcast create(String title, Long authorId) {
        requireAuthor(authorId);
        Podcast podcast = new Podcast();
        podcast.setTitle(title);
        podcast.setAuthorId(authorId);
        podcast.setStatus(Podcast.Status.ACTIVE);
        podcast.setCreatedAt(Instant.now(clock));
        return podcasts.save(podcast);
    }

    public Podcast update(Long id, String title, Long authorId, Podcast.Status status) {
        Podcast podcast = findById(id);
        if (title != null) podcast.setTitle(title);
        if (authorId != null) {
            requireAuthor(authorId);
            podcast.setAuthorId(authorId);
        }
        if (status != null) podcast.setStatus(status);
        return podcasts.save(podcast);
    }

    public void delete(Long id) {
        findById(id);
        episodes.deleteByPodcastId(id);
        podcasts.deleteById(id);
    }

    public void deleteByAuthorId(Long authorId) {
        podcasts.findByAuthorId(authorId).stream()
                .map(BaseEntity::getId)
                .forEach(this::delete);
    }

    private void requireAuthor(Long authorId) {
        if (!users.existsById(authorId)) {
            throw new BadRequestException("Author " + authorId + " does not exist");
        }
    }
}