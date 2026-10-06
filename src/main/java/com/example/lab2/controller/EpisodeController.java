package com.example.lab2.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.lab2.dto.EpisodeDto;
import com.example.lab2.model.Episode;
import com.example.lab2.service.EpisodeService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/episodes")
public class EpisodeController {

    private final EpisodeService service;

    public EpisodeController(EpisodeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EpisodeDto.Response> getAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public EpisodeDto.Response getById(@PathVariable Long id) {
        return toResponse(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EpisodeDto.Response create(@Valid @RequestBody EpisodeDto.CreateRequest request) {
        return toResponse(service.create(request.podcastId(), request.title(), request.durationSeconds()));
    }

    @PatchMapping("/{id}")
    public EpisodeDto.Response update(@PathVariable Long id, @Valid @RequestBody EpisodeDto.UpdateRequest request) {
        return toResponse(service.update(id, request.title(), request.durationSeconds(), request.state()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private EpisodeDto.Response toResponse(Episode e) {
        return new EpisodeDto.Response(e.getId(), e.getPodcastId(), e.getTitle(),
                e.getDurationSeconds(), e.getState(), e.getCreatedAt());
    }
}