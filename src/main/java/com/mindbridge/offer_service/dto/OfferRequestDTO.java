package com.mindbridge.offer_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferRequestDTO {
    @NotBlank
    private String title;
    @NotNull
    private LocalDate startDate;
    @NotNull
    @FutureOrPresent
    private LocalDate endDate;

}
