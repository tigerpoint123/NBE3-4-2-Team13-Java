package com.app.backend.domain.group.service;

import java.util.Optional;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.GroupLike;
import com.app.backend.domain.group.exception.GroupLikeErrorCode;
import com.app.backend.domain.group.exception.GroupLikeException;
import com.app.backend.domain.group.repository.GroupLikeRepository;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupLikeService {

    private final GroupLikeRepository groupLikeRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean toggleLikeGroup(final Long groupId, final Long memberId) {
        Group group = groupRepository.findByIdWithLock(groupId)
                .orElseThrow(() -> new GroupLikeException(GroupLikeErrorCode.GROUP_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GroupLikeException(GroupLikeErrorCode.MEMBER_NOT_FOUND));

        Optional<GroupLike> existingLike = groupLikeRepository.findByGroupAndMemberWithLock(group, member);

        if (existingLike.isPresent()) {
            groupLikeRepository.delete(existingLike.get());
            group.decreaseLikeCount();
            return false; // 좋아요 취소
        } else {
            GroupLike newLike = GroupLike.builder()
                    .group(group)
                    .member(member)
                    .build();

            groupLikeRepository.save(newLike);
            group.increaseLikeCount();
            return true; // 좋아요 성공
        }
    }
}