package com.mindbridge.offer_service.model.entity;

import com.mindbridge.offer_service.model.enums.OfferStatus;
import com.mindbridge.offer_service.model.enums.PatientDiscount;
import com.mindbridge.offer_service.model.enums.VisibilityMultiplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
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
    @Column(columnDefinition = "TEXT")
    private String description;
    @ElementCollection
    @CollectionTable(name = "offer_benefits", joinColumns = @JoinColumn(name = "offer_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> benefits;
    private VisibilityMultiplier boostMultiplier;
    private PatientDiscount discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private OfferStatus status;
    @Version
    private Long version;
    private UUID psychologistId;

    public void subscribe(UUID psychologistId) {
        if (!isOpen()) {
            throw new IllegalStateException("La oferta no está en estado OPEN");
        }
        if (!isInDateRange()) {
            throw new IllegalStateException("La oferta aún no está habilitada o ya venció");
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
        return isOpen() && isInDateRange();
    }

    private boolean isOpen() {
        return status.equals(OfferStatus.OPEN);
    }

    private boolean isInDateRange() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }
}
