package com.app.backend.domain.group.repository;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.GroupLike;
import com.app.backend.domain.member.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupLikeRepository extends JpaRepository<GroupLike, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 좋아요 중복 방지
    @Query("SELECT gl FROM GroupLike gl WHERE gl.group = :group AND gl.member = :member")
    Optional<GroupLike> findByGroupAndMemberWithLock(Group group, Member member);

    long countByGroupIdAndMemberId(Long groupId, Long memberId);

    boolean existsByGroupAndMember(Group group, Member member);
}
