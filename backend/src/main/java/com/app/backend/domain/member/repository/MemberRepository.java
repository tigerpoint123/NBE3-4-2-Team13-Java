package com.app.backend.domain.member.repository;

import com.app.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String > {

}
