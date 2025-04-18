package org.platform.repository.verification;

import org.platform.entity.verification.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByEmail(String email);

    void deleteByEmail(String email);
    void deleteById(UUID id);
}
