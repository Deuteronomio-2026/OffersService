package com.mindbridge.offer_service.service;

import com.mindbridge.offer_service.dto.OfferRequestDTO;
import com.mindbridge.offer_service.dto.OfferResponseDTO;
import com.mindbridge.offer_service.model.entity.Offer;
import com.mindbridge.offer_service.model.enums.OfferStatus;
import com.mindbridge.offer_service.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository offerRepository;

    public OfferResponseDTO createOffer(OfferRequestDTO dto){
        Offer offer = Offer.builder().
                title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(OfferStatus.OPEN)
                .build();
        Offer saved = offerRepository.save(offer);
        return toResponseDTO(saved);
    }

    public OfferResponseDTO cancelOffer(UUID id){
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));
        offer.cancel();
        Offer saved = offerRepository.save(offer);
        return toResponseDTO(saved)


    }

    public OfferResponseDTO subscribePsychologist(UUID offerId, UUID psychologistId){
        if(offerRepository.existsByPsychologistIdAndStatus(psychologistId,OfferStatus.TAKEN)){
            throw new IllegalStateException("El psicólogo ya tiene una oferta activa");
        }
        Offer offer = offerRepository.findByIdWithLock(offerId)
                    .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));
        offer.subscribe(psychologistId);
        Offer saved = offerRepository.save(offer);
        return toResponseDTO(saved);
    }

    public List<OfferResponseDTO> getActiveOffers(){
        List<Offer> offers = offerRepository.findByStatus(OfferStatus.OPEN);
        return offers.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private OfferResponseDTO toResponseDTO(Offer offer) {
        return OfferResponseDTO.builder()
                .id(offer.getId())
                .title(offer.getTitle())
                .startDate(offer.getStartDate())
                .endDate(offer.getEndDate())
                .status(offer.getStatus())
                .psychologistId(offer.getPsychologistId())
                .build();
    }
}
