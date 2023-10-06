package com.example.mockmvcintframework.repository;

import com.example.mockmvcintframework.domain.Accommodation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryAccommodationRepository implements AccommodationRepository {
    private final HashMap<Long, Accommodation> entities = new HashMap<>();

    @Override
    public Optional<Accommodation> create(Accommodation entity) {
        Long id = entities.size() + 1L;

        Optional.of(entity.withId(id)).ifPresent(e -> entities.put(e.getId(), e));

        return Optional.of(entities.get(id));
    }

    @Override
    public Optional<Accommodation> getById(Long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Optional<List<Accommodation>> findAll() {
        return Optional.of(new ArrayList<>(entities.values()));
    }

    @Override
    public Optional<Accommodation> update(Long id, Accommodation accommodation) {
        getById(id).ifPresent(e -> entities.replace(id, accommodation.withId(id)));

        return Optional.of(entities.get(id));
    }

    @Override
    public void delete(Long id) {
        entities.remove(id);
    }

    @Override
    public void deleteAll() {
        entities.clear();
    }
}
