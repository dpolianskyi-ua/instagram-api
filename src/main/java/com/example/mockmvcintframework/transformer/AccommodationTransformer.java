package com.example.mockmvcintframework.transformer;

import com.example.mockmvcintframework.domain.Accommodation;
import com.example.mockmvcintframework.dto.AccommodationDTO;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
public class AccommodationTransformer {

    public AccommodationDTO toDto(Accommodation entity) {
        if (isNull(entity)) {
            return null;
        }

        return AccommodationDTO.builder()
                .id(entity.getId())
                .addressId(entity.getAddressId())
                .personId(entity.getPersonId())
                .accommodationDate(entity.getAccommodationDate())
                .isSingleOwned(entity.isSingleOwned())
                .build();
    }

    public List<AccommodationDTO> toDto(List<Accommodation> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public Accommodation toDomain(AccommodationDTO dto) {
        if (isNull(dto)) {
            return null;
        }

        return Accommodation.builder()
                .id(dto.getId())
                .addressId(dto.getAddressId())
                .personId(dto.getPersonId())
                .accommodationDate(dto.getAccommodationDate())
                .isSingleOwned(dto.getIsSingleOwned())
                .build();
    }

    public List<Accommodation> toDomain(List<AccommodationDTO> dtos) {
        return dtos.stream().map(this::toDomain).toList();
    }
}
