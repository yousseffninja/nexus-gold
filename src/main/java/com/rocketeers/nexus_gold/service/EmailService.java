package com.rocketeers.nexus_gold.service;

public interface EmailService {
    void sendVerificationCode(String to, String code);
}