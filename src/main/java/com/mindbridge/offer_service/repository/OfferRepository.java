package com.mindbridge.offer_service.repository;

import com.mindbridge.offer_service.model.entity.Offer;
import com.mindbridge.offer_service.model.enums.OfferStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

    List<Offer> findByStatus(OfferStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Offer o WHERE o.id = :id")
    Optional<Offer> findByIdWithLock(@Param("id") UUID id);

    boolean existsByPsychologistIdAndStatus(UUID psychologistId, OfferStatus status);
}
