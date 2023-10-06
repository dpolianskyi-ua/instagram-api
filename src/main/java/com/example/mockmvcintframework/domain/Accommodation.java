package com.example.mockmvcintframework.domain;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDate;

@Data
@Builder
@With
public class Accommodation {
    private Long id;
    private Long addressId;
    private Long personId;
    private LocalDate accommodationDate;
    private boolean isSingleOwned;
}
