package com.mindbridge.offer_service.model.entity;

import com.mindbridge.offer_service.model.enums.OfferStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "offers")
@Data //Genera automáticamente todos los getters y setters
@Builder
@NoArgsConstructor // Genera constructor sin parámetros
@AllArgsConstructor // Genera un constructor con todos los parámetros
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private OfferStatus status;
    @Version
    private Long version;
    private UUID psychologistId;

    public void subscribe(UUID psychologistId) {
        if (!isAvailable()) {
            throw new IllegalStateException("La oferta no está disponible");
        }
        this.psychologistId = psychologistId;
        this.status = OfferStatus.TAKEN;
    }

    public void cancel() {
        if (status.equals(OfferStatus.CANCELLED)) {
            throw new IllegalStateException("La oferta ya está cancelada");
        }
        status = OfferStatus.CANCELLED;
    }

    public boolean isAvailable() {
        LocalDate today = LocalDate.now();
        return status.equals(OfferStatus.OPEN)
                && !today.isBefore(startDate)
                && !today.isAfter(endDate);
    }
}
