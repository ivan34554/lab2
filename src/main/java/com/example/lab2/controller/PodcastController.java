package com.example.lab2.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.lab2.dto.PodcastDto;
import com.example.lab2.model.Podcast;
import com.example.lab2.service.PodcastService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/podcasts")
public class PodcastController {

    private final PodcastService service;

    public PodcastController(PodcastService service) {
        this.service = service;
    }

    @GetMapping
    public List<PodcastDto.Response> getAll() {
        return service.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PodcastDto.Response getById(@PathVariable Long id) {
        return toResponse(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PodcastDto.Response create(@Valid @RequestBody PodcastDto.CreateRequest request) {
        return toResponse(service.create(request.title(), request.authorId()));
    }

    @PatchMapping("/{id}")
    public PodcastDto.Response update(@PathVariable Long id, @Valid @RequestBody PodcastDto.UpdateRequest request) {
        return toResponse(service.update(id, request.title(), request.authorId(), request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private PodcastDto.Response toResponse(Podcast p) {
        return new PodcastDto.Response(p.getId(), p.getTitle(), p.getAuthorId(), p.getStatus(), p.getCreatedAt());
    }
}