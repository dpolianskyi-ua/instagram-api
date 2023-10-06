package com.example.mockmvcintframework.service;

import com.example.mockmvcintframework.domain.Accommodation;
import com.example.mockmvcintframework.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository repository;

    public Optional<Accommodation> create(Accommodation entity) {
        return repository.create(entity);
    }

    public Optional<Accommodation> getById(Long id) {
        return repository.getById(id);
    }

    public Optional<List<Accommodation>> findAll() {
        return repository.findAll();
    }

    public Optional<Accommodation> update(Long id, Accommodation entity) {
        return repository.update(id, entity);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
