package com.mindbridge.offer_service.model.enums;

import lombok.Getter;

@Getter
public enum VisibilityMultiplier {
    X2(2),
    X3(3),
    X4(4),
    X5(5);

    private final int value;

    VisibilityMultiplier(int value) {
        this.value = value;
    }

    public static VisibilityMultiplier fromValue(int value) {
        for (VisibilityMultiplier v : values()) {
            if (v.value == value) return v;
        }
        throw new IllegalArgumentException("Multiplicador inválido: " + value);
    }
}