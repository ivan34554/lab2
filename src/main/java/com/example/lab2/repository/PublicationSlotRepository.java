package com.example.lab2.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.lab2.model.PublicationSlot;

@Repository
public class PublicationSlotRepository extends InMemoryRepository<PublicationSlot> {

    public List<PublicationSlot> findByEpisodeId(Long episodeId) {
        return findAll().stream().filter(s -> s.getEpisodeId().equals(episodeId)).toList();
    }
}