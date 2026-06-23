package com.rocketeers.nexus_gold.repository;

import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    List<VerificationCode> findByUser(User user);
    Optional<VerificationCode> findByUserAndTypeAndUsedFalse(User user, VerificationCode.VerificationCodeType type);
    void deleteByUser(User user);
}
