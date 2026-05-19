package com.mindbridge.offer_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbridge.offer_service.dto.OfferRequestDTO;
import com.mindbridge.offer_service.dto.OfferResponseDTO;
import com.mindbridge.offer_service.dto.SubscribeRequestDTO;
import com.mindbridge.offer_service.model.enums.OfferStatus;
import com.mindbridge.offer_service.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfferService offerService;

    private OfferResponseDTO offerResponse;
    private UUID offerId;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        offerResponse = OfferResponseDTO.builder()
                .id(offerId)
                .title("Oferta Marzo")
                .description("Descripción test")
                .status(OfferStatus.OPEN)
                .boostMultiplier(3)
                .discountPercent(20)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .build();
    }

    @Test
    void createOffer_debeRetornar201() throws Exception {
        OfferRequestDTO dto = new OfferRequestDTO();
        dto.setTitle("Oferta Marzo");
        dto.setDescription("Descripción test");
        dto.setBenefits(List.of("Beneficio 1"));
        dto.setBoostMultiplier(3);
        dto.setDiscountPercent(20);
        dto.setStartDate(LocalDate.now().minusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(10));

        when(offerService.createOffer(any())).thenReturn(offerResponse);

        mockMvc.perform(post("/api/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Oferta Marzo"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void cancelOffer_debeRetornar200() throws Exception {
        offerResponse.setStatus(OfferStatus.CANCELLED);
        when(offerService.cancelOffer(offerId)).thenReturn(offerResponse);

        mockMvc.perform(patch("/api/offers/" + offerId + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void subscribePsychologist_debeRetornar200() throws Exception {
        SubscribeRequestDTO dto = new SubscribeRequestDTO();
        dto.setPsychologistId(UUID.randomUUID());

        offerResponse.setStatus(OfferStatus.TAKEN);
        when(offerService.subscribePsychologist(any(), any())).thenReturn(offerResponse);

        mockMvc.perform(post("/api/offers/" + offerId + "/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TAKEN"));
    }

    @Test
    void getActiveOffers_debeRetornarLista() throws Exception {
        when(offerService.getActiveOffers()).thenReturn(List.of(offerResponse));

        mockMvc.perform(get("/api/offers/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Oferta Marzo"));
    }

    @Test
    void getTakenOffers_debeRetornarLista() throws Exception {
        offerResponse.setStatus(OfferStatus.TAKEN);
        when(offerService.getTakenOffers()).thenReturn(List.of(offerResponse));

        mockMvc.perform(get("/api/offers/taken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("TAKEN"));
    }

    @Test
    void deleteOffer_debeRetornar204() throws Exception {
        doNothing().when(offerService).deleteOffer(offerId);

        mockMvc.perform(delete("/api/offers/" + offerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllOffers_debeRetornarLista() throws Exception {
        when(offerService.getAll()).thenReturn(List.of(offerResponse));

        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Oferta Marzo"));
    }
}