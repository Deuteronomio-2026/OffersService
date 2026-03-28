package com.mindbridge.offer_service.service;

import com.mindbridge.offer_service.dto.OfferRequestDTO;
import com.mindbridge.offer_service.dto.OfferResponseDTO;
import com.mindbridge.offer_service.model.entity.Offer;
import com.mindbridge.offer_service.model.enums.OfferStatus;
import com.mindbridge.offer_service.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferService offerService;

    private Offer offerOpen;
    private UUID offerId;
    private UUID psychologistId;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        psychologistId = UUID.randomUUID();
        offerOpen = Offer.builder()
                .id(offerId)
                .title("Oferta Marzo")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .status(OfferStatus.OPEN)
                .psychologistId(null)
                .version(0L)
                .build();
    }

    @Test
    void createOffer_debeRetornarOfertaCreada() {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setTitle("Oferta Marzo");
        dto.setStartDate(LocalDate.now().minusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(10));

        when(offerRepository.save(any())).thenReturn(offerOpen);

        OfferResponseDTO response = offerService.createOffer(dto);

        assertNotNull(response);
        assertEquals("Oferta Marzo", response.getTitle());
        assertEquals(OfferStatus.OPEN, response.getStatus());
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    void cancelOffer_debeRetornarOfertaCancelada() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerOpen));
        when(offerRepository.save(any())).thenReturn(offerOpen);

        OfferResponseDTO response = offerService.cancelOffer(offerId);

        assertNotNull(response);
        assertEquals(OfferStatus.CANCELLED, response.getStatus());
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    void cancelOffer_cuandoNoExiste_debeLanzarExcepcion() {
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> offerService.cancelOffer(offerId));
    }

    @Test
    void subscribePsychologist_debeAsignarPsicologo() {
        when(offerRepository.existsByPsychologistIdAndStatus(psychologistId, OfferStatus.TAKEN))
                .thenReturn(false);
        when(offerRepository.findByIdWithLock(offerId)).thenReturn(Optional.of(offerOpen));
        when(offerRepository.save(any())).thenReturn(offerOpen);

        OfferResponseDTO response = offerService.subscribePsychologist(offerId, psychologistId);

        assertNotNull(response);
        verify(offerRepository, times(1)).findByIdWithLock(offerId);
        verify(offerRepository, times(1)).save(any());
    }

    @Test
    void subscribePsychologist_cuandoPsicologoYaTieneOferta_debeLanzarExcepcion() {
        when(offerRepository.existsByPsychologistIdAndStatus(psychologistId, OfferStatus.TAKEN))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> offerService.subscribePsychologist(offerId, psychologistId));
    }

    @Test
    void getActiveOffers_debeRetornarListaDeOfertas() {
        when(offerRepository.findByStatus(OfferStatus.OPEN)).thenReturn(List.of(offerOpen));

        List<OfferResponseDTO> response = offerService.getActiveOffers();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(OfferStatus.OPEN, response.get(0).getStatus());
        verify(offerRepository, times(1)).findByStatus(OfferStatus.OPEN);
    }
}