package com.example.lab2.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.example.lab2.model.Podcast;

@Repository
public class PodcastRepository extends InMemoryRepository<Podcast> {

    public List<Podcast> findByAuthorId(Long authorId) {
        return findAll().stream().filter(p -> p.getAuthorId().equals(authorId)).toList();
    }
}