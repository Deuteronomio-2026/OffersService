package com.mindbridge.offer_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.mindbridge.offer_service.controller.OfferController;
import com.mindbridge.offer_service.service.OfferService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfferService offerService;

    @Test
    void cuandoIllegalStateException_debeRetornar409() throws Exception {
        when(offerService.cancelOffer(any()))
                .thenThrow(new IllegalStateException("El psicólogo ya tiene una oferta activa"));

        mockMvc.perform(patch("/api/offers/" + UUID.randomUUID() + "/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflicto"));
    }

    @Test
    void cuandoRuntimeException_debeRetornar404() throws Exception {
        when(offerService.cancelOffer(any()))
                .thenThrow(new RuntimeException("Oferta no encontrada"));

        mockMvc.perform(patch("/api/offers/" + UUID.randomUUID() + "/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"));
    }
}