package com.example.mockmvcintframework.controller;

import com.example.mockmvcintframework.domain.Accommodation;
import com.example.mockmvcintframework.dto.AccommodationDTO;
import com.example.mockmvcintframework.service.AccommodationService;
import com.example.mockmvcintframework.transformer.AccommodationTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationTransformer transformer;
    private final AccommodationService service;

    @PostMapping
    public ResponseEntity<AccommodationDTO> create(@RequestBody AccommodationDTO dto) {
        Accommodation entity = transformer.toDomain(dto);

        return service.create(entity)
                .map(transformer::toDto)
                .map(e -> new ResponseEntity<>(e, CREATED))
                .orElseGet(() -> new ResponseEntity<>(UNPROCESSABLE_ENTITY));
    }

    @GetMapping("{id}")
    public ResponseEntity<AccommodationDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(transformer::toDto)
                .map(accommodation -> new ResponseEntity<>(accommodation, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> findAll() {
        return service.findAll()
                .map(accommodations -> new ResponseEntity<>(transformer.toDto(accommodations), OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @PutMapping("{id}")
    public ResponseEntity<AccommodationDTO> update(@PathVariable Long id, @RequestBody AccommodationDTO dto) {
        Accommodation entity = transformer.toDomain(dto);

        return service.update(id, entity)
                .map(transformer::toDto)
                .map(updatedAccommodation -> new ResponseEntity<>(updatedAccommodation, OK))
                .orElseGet(() -> new ResponseEntity<>(UNPROCESSABLE_ENTITY));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<AccommodationDTO> delete(@PathVariable Long id) {
        service.delete(id);

        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<AccommodationDTO> deleteAll() {
        service.deleteAll();

        return new ResponseEntity<>(NO_CONTENT);
    }
}
