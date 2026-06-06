package com.rocketeers.nexus_gold.service;

public interface EmailService {
    void sendVerificationCode(String to, String code);

    void sendPasswordResetCode(String to, String code);
}