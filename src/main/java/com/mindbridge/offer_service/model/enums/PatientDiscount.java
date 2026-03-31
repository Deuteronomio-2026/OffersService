package com.mindbridge.offer_service.model.enums;

import lombok.Getter;

@Getter
public enum PatientDiscount {
    TEN(10),
    FIFTEEN(15),
    TWENTY(20),
    TWENTY_FIVE(25);

    private final int value;

    PatientDiscount(int value) {
        this.value = value;
    }

    public static PatientDiscount fromValue(int value) {
        for (PatientDiscount d : values()) {
            if (d.value == value) return d;
        }
        throw new IllegalArgumentException("Descuento inválido: " + value);
    }
}