package com.app.backend.domain.member.repository;

import com.app.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String > {

    Optional<Member> findByUsername(String username);
}
