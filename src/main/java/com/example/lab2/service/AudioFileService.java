package com.example.lab2.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.lab2.exception.BadRequestException;
import com.example.lab2.exception.NotFoundException;
import com.example.lab2.model.AudioFile;
import com.example.lab2.model.BaseEntity;
import com.example.lab2.repository.AudioFileRepository;
import com.example.lab2.repository.EpisodeRepository;

@Service
public class AudioFileService {

    private final AudioFileRepository audioFiles;
    private final EpisodeRepository episodes;
    private final Clock clock;

    public AudioFileService(AudioFileRepository audioFiles, EpisodeRepository episodes, Clock clock) {
        this.audioFiles = audioFiles;
        this.episodes = episodes;
        this.clock = clock;
    }

    public List<AudioFile> findAll() {
        return audioFiles.findAll();
    }

    public AudioFile findById(Long id) {
        return audioFiles.findById(id)
                .orElseThrow(() -> new NotFoundException("Audio file " + id + " not found"));
    }

    public AudioFile create(Long episodeId, String path, AudioFile.Format format,
                            long sizeBytes, boolean main) {
        if (!episodes.existsById(episodeId)) {
            throw new BadRequestException("Episode " + episodeId + " does not exist");
        }
        if (main) {
            resetMain(episodeId, null);
        }
        AudioFile file = new AudioFile();
        file.setEpisodeId(episodeId);
        file.setPath(path);
        file.setFormat(format);
        file.setSizeBytes(sizeBytes);
        file.setMain(main);
        file.setCreatedAt(Instant.now(clock));
        return audioFiles.save(file);
    }

    public AudioFile update(Long id, String path, AudioFile.Format format,
                            Long sizeBytes, Boolean main) {
        AudioFile file = findById(id);
        if (path != null) file.setPath(path);
        if (format != null) file.setFormat(format);
        if (sizeBytes != null) file.setSizeBytes(sizeBytes);
        if (main != null) {
            if (main) {
                resetMain(file.getEpisodeId(), id);
            }
            file.setMain(main);
        }
        return audioFiles.save(file);
    }

    public void delete(Long id) {
        findById(id);
        audioFiles.deleteById(id);
    }

    public void deleteByEpisodeId(Long episodeId) {
        audioFiles.findByEpisodeId(episodeId).stream()
                .map(BaseEntity::getId)
                .forEach(audioFiles::deleteById);
    }

    /** У эпизода может быть только один основной файл. */
    private void resetMain(Long episodeId, Long exceptId) {
        for (AudioFile other : audioFiles.findByEpisodeId(episodeId)) {
            if (other.isMain() && !other.getId().equals(exceptId)) {
                other.setMain(false);
                audioFiles.save(other);
            }
        }
    }
}