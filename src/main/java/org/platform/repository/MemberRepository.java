package org.platform.repository;

import org.platform.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);

    Member getByUsername(String username);
    boolean existsByEmail(String email);

    Member getByEmail(String email);
}
