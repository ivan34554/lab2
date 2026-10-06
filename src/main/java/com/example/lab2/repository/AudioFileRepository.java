package com.example.lab2.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.lab2.model.AudioFile;

@Repository
public class AudioFileRepository extends InMemoryRepository<AudioFile> {

    public List<AudioFile> findByEpisodeId(Long episodeId) {
        return findAll().stream().filter(a -> a.getEpisodeId().equals(episodeId)).toList();
    }
}