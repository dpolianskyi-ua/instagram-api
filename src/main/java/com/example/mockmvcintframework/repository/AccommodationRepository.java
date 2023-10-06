package com.example.mockmvcintframework.repository;

import com.example.mockmvcintframework.domain.Accommodation;

import java.util.List;
import java.util.Optional;

public interface AccommodationRepository {
    Optional<Accommodation> create(Accommodation accommodation);

    Optional<Accommodation> getById(Long id);

    Optional<List<Accommodation>> findAll();

    Optional<Accommodation> update(Long id, Accommodation accommodation);

    void delete(Long id);

    void deleteAll();
}
