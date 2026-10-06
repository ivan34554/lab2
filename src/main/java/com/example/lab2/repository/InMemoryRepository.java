package com.example.lab2.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.example.lab2.model.BaseEntity;

public abstract class InMemoryRepository<T extends BaseEntity> {

    private final Map<Long, T> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<T> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(BaseEntity::getId))
                .toList();
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.incrementAndGet());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }
}