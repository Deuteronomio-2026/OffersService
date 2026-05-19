package com.mindbridge.offer_service.model.entity;

import com.mindbridge.offer_service.model.enums.OfferStatus;
import com.mindbridge.offer_service.model.enums.PatientDiscount;
import com.mindbridge.offer_service.model.enums.VisibilityMultiplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferTest {

    private Offer offer;

    @BeforeEach
    void setUp() {
        offer = Offer.builder()
                .id(UUID.randomUUID())
                .title("Oferta Test")
                .description("Descripción")
                .benefits(List.of("Beneficio 1"))
                .boostMultiplier(VisibilityMultiplier.X3)
                .discountPercent(PatientDiscount.TWENTY)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .status(OfferStatus.OPEN)
                .version(0L)
                .build();
    }

    // ─── subscribe ────────────────────────────────────────────────────────────

    @Test
    void subscribe_cuandoOfertaOpen_debeAsignarPsicologo() {
        UUID psychologistId = UUID.randomUUID();
        offer.subscribe(psychologistId);
        assertEquals(OfferStatus.TAKEN, offer.getStatus());
        assertEquals(psychologistId, offer.getPsychologistId());
    }

    @Test
    void subscribe_cuandoOfertaNoEstaOpen_debeLanzarExcepcion() {
        offer.setStatus(OfferStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> offer.subscribe(UUID.randomUUID()));
    }

    @Test
    void subscribe_cuandoFueraDeRangoDeFechas_debeLanzarExcepcion() {
        offer.setStartDate(LocalDate.now().plusDays(5));
        assertThrows(IllegalStateException.class, () -> offer.subscribe(UUID.randomUUID()));
    }

    @Test
    void subscribe_cuandoOfertaVencida_debeLanzarExcepcion() {
        offer.setEndDate(LocalDate.now().minusDays(1));
        assertThrows(IllegalStateException.class, () -> offer.subscribe(UUID.randomUUID()));
    }

    // ─── cancel ───────────────────────────────────────────────────────────────

    @Test
    void cancel_cuandoOfertaOpen_debeCancelar() {
        offer.cancel();
        assertEquals(OfferStatus.CANCELLED, offer.getStatus());
    }

    @Test
    void cancel_cuandoYaEstaCancelada_debeLanzarExcepcion() {
        offer.setStatus(OfferStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> offer.cancel());
    }

    // ─── isAvailable ──────────────────────────────────────────────────────────

    @Test
    void isAvailable_cuandoOpenYEnRango_debeRetornarTrue() {
        assertTrue(offer.isAvailable());
    }

    @Test
    void isAvailable_cuandoCancelled_debeRetornarFalse() {
        offer.setStatus(OfferStatus.CANCELLED);
        assertFalse(offer.isAvailable());
    }

    @Test
    void isAvailable_cuandoFueraDeRango_debeRetornarFalse() {
        offer.setEndDate(LocalDate.now().minusDays(1));
        assertFalse(offer.isAvailable());
    }

    // ─── getters/setters (cubre DTOs indirectamente) ──────────────────────────

    @Test
    void builder_debeConstruirOfertaCorrectamente() {
        assertNotNull(offer.getId());
        assertEquals("Oferta Test", offer.getTitle());
        assertEquals(OfferStatus.OPEN, offer.getStatus());
        assertEquals(VisibilityMultiplier.X3, offer.getBoostMultiplier());
        assertEquals(PatientDiscount.TWENTY, offer.getDiscountPercent());
    }
}