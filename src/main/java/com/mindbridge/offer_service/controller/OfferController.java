package com.mindbridge.offer_service.controller;

import com.mindbridge.offer_service.dto.OfferRequestDTO;
import com.mindbridge.offer_service.dto.OfferResponseDTO;
import com.mindbridge.offer_service.dto.SubscribeRequestDTO;
import com.mindbridge.offer_service.service.OfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Offers", description = "Gestión de ofertas para psicólogos en MindBridge")
public class OfferController {

    private final OfferService offerService;

    @Operation(
            summary = "Crear oferta",
            description = "El administrador crea una nueva oferta con fechas y título. Nace en estado OPEN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Oferta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<OfferResponseDTO> createOffer(@Valid @RequestBody OfferRequestDTO dto) {
        OfferResponseDTO response = offerService.createOffer(dto);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "Cancelar oferta",
            description = "El administrador cancela una oferta existente. Cambia el estado a CANCELLED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oferta cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada"),
            @ApiResponse(responseCode = "400", description = "La oferta ya está cancelada")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OfferResponseDTO> cancelOffer(
            @Parameter(description = "ID de la oferta a cancelar") @PathVariable UUID id) {
        OfferResponseDTO response = offerService.cancelOffer(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Suscribirse a oferta",
            description = "Un psicólogo se suscribe a una oferta disponible. Solo un psicólogo puede tomar la oferta. Maneja concurrencia con optimistic locking."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suscripción exitosa"),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Oferta no disponible o psicólogo ya tiene una oferta activa")
    })
    @PostMapping("/{offerId}/subscribe")
    public ResponseEntity<OfferResponseDTO> subscribePsychologist(
            @Parameter(description = "ID de la oferta") @PathVariable UUID offerId,
            @Valid @RequestBody SubscribeRequestDTO dto) {
        OfferResponseDTO response = offerService.subscribePsychologist(offerId, dto.getPsychologistId());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Ver ofertas activas",
            description = "Retorna todas las ofertas en estado OPEN disponibles para los psicólogos."
    )
    @ApiResponse(responseCode = "200", description = "Lista de ofertas activas")
    @GetMapping("/active")
    public ResponseEntity<List<OfferResponseDTO>> getActiveOffers() {
        List<OfferResponseDTO> response = offerService.getActiveOffers();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Ver psicólogos en promoción",
            description = "Retorna todas las ofertas TAKEN — psicólogos actualmente en oferta visibles para el paciente."
    )
    @ApiResponse(responseCode = "200", description = "Lista de ofertas tomadas")
    @GetMapping("/taken")
    public ResponseEntity<List<OfferResponseDTO>> getTakenOffers() {
        return ResponseEntity.ok(offerService.getTakenOffers());
    }

    @Operation(
            summary = "Eliminar oferta",
            description = "Elimina permanentemente una oferta existente. Solo administradores."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Oferta eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Oferta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(
            @Parameter(description = "ID de la oferta a eliminar") @PathVariable UUID id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener todas las ofertas", description = "Retorna todas las ofertas sin filtrar (solo administradores)")
    @GetMapping
    public ResponseEntity<List<OfferResponseDTO>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAll());
    }



}