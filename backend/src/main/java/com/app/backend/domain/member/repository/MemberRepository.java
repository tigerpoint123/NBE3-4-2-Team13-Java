package com.app.backend.domain.member.repository;

import com.app.backend.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String > {

    Optional<Member> findByUsername(@NotBlank(message = "아이디는 필수입니다.") String username);
}
