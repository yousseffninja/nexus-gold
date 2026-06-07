package com.rocketeers.nexus_gold.service;

public class Util {
    public boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
