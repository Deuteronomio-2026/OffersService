package com.mindbridge.offer_service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OfferRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private List<String> benefits;
    @NotNull
    private int boostMultiplier;
    @NotNull
    private int discountPercent;
    @NotNull
    private LocalDate startDate;
    @NotNull
    @FutureOrPresent
    private LocalDate endDate;

}
