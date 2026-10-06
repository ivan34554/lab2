package com.example.lab2.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.lab2.model.Episode;

@Repository
public class EpisodeRepository extends InMemoryRepository<Episode> {

    public List<Episode> findByPodcastId(Long podcastId) {
        return findAll().stream().filter(e -> e.getPodcastId().equals(podcastId)).toList();
    }
}