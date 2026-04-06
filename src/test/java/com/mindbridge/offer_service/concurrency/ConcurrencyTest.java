package com.mindbridge.offer_service.concurrency;

import com.mindbridge.offer_service.model.entity.Offer;
import com.mindbridge.offer_service.model.enums.OfferStatus;
import com.mindbridge.offer_service.model.enums.PatientDiscount;
import com.mindbridge.offer_service.model.enums.VisibilityMultiplier;
import com.mindbridge.offer_service.repository.OfferRepository;
import com.mindbridge.offer_service.service.OfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ConcurrencyTest {

    @Autowired
    private OfferService offerService;

    @Autowired
    private OfferRepository offerRepository;

    private UUID offerId;

    @BeforeEach
    void setUp() {
        offerRepository.deleteAll();
        Offer offer = Offer.builder()
                .title("Oferta concurrencia")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(10))
                .status(OfferStatus.OPEN)
                .boostMultiplier(VisibilityMultiplier.X3)
                .discountPercent(PatientDiscount.TWENTY)
                .version(0L)
                .build();
        offerId = offerRepository.save(offer).getId();
    }

    @Test
    void soloUnPsicologoDebeGanar() throws InterruptedException {
        int hilos = 10;
        ExecutorService executor = Executors.newFixedThreadPool(hilos);
        CountDownLatch latch = new CountDownLatch(hilos);
        AtomicInteger exitosos = new AtomicInteger(0);
        AtomicInteger fallidos = new AtomicInteger(0);

        for (int i = 0; i < hilos; i++) {
            executor.submit(() -> {
                try {
                    offerService.subscribePsychologist(offerId, UUID.randomUUID());
                    exitosos.incrementAndGet();
                } catch (Exception e) {
                    fallidos.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, exitosos.get(), "Solo un psicólogo debe ganar la oferta");
        assertEquals(9, fallidos.get(), "Los otros 9 deben fallar");
    }
}