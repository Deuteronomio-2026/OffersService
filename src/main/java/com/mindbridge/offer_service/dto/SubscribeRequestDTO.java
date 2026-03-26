package com.mindbridge.offer_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SubscribeRequestDTO {
    @NotNull
    private UUID psychologistId;
}
