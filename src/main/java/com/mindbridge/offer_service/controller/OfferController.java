package com.mindbridge.offer_service.controller;

import com.mindbridge.offer_service.dto.OfferRequestDTO;
import com.mindbridge.offer_service.dto.OfferResponseDTO;
import com.mindbridge.offer_service.dto.SubscribeRequestDTO;
import com.mindbridge.offer_service.model.entity.Offer;
import com.mindbridge.offer_service.service.OfferService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService offerService;
    @PostMapping
    public ResponseEntity<OfferResponseDTO> createOffer(@Valid @RequestBody OfferRequestDTO dto){
        OfferResponseDTO response = offerService.createOffer(dto);
        return ResponseEntity.status(201).body(response);
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OfferResponseDTO> cancelOffer(@PathVariable UUID id) {
        OfferResponseDTO response = offerService.cancelOffer(id);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{offerId}/subscribe")
    public ResponseEntity<OfferResponseDTO> subscribePsychologist(
            @PathVariable UUID offerId,
            @Valid @RequestBody SubscribeRequestDTO dto) {
        OfferResponseDTO response = offerService.subscribePsychologist(offerId, dto.getPsychologistId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<OfferResponseDTO>> getActiveOffers() {
        List<OfferResponseDTO> response = offerService.getActiveOffers();
        return ResponseEntity.ok(response);

    }



}
