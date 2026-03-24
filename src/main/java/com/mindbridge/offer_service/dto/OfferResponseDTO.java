package com.mindbridge.offer_service.dto;

import com.mindbridge.offer_service.model.enums.OfferStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;
@Data
@Builder
public class OfferResponseDTO {
    private UUID id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private OfferStatus status;
    private UUID psychologistId;
}
