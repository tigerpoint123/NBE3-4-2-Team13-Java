package com.app.backend.domain.group.service;

import com.app.backend.domain.category.entity.Category;
import com.app.backend.domain.category.repository.CategoryRepository;
import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.domain.group.repository.GroupLikeRepository;
import com.app.backend.domain.group.repository.GroupRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GroupLikeServiceTest {

    @Autowired
    private GroupLikeService groupLikeService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupLikeRepository groupLikeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Group testGroup;
    private List<Member> testMembers;
    private Category category;

    @BeforeEach
    void setup() {
        groupLikeRepository.deleteAll();
        groupRepository.deleteAll();
        memberRepository.deleteAll();
        categoryRepository.deleteAll();

        category = categoryRepository.save(Category.builder()
                .name("category")
                .build());

        testGroup = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .province("test province")
                        .city("test city")
                        .town("test town")
                        .description("test description")
                        .recruitStatus(RecruitStatus.RECRUITING)
                        .maxRecruitCount(300)
                        .category(category)
                        .build());

        testMembers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Member member = memberRepository.save(
                    Member.builder()
                            .username("user" + i)
                            .build()
            );
            testMembers.add(member);

        }
    }

    @Test
    @DisplayName("그룹 좋아요")
    void t1() throws Exception {
        Long memberId = testMembers.get(0).getId();
        Long groupId = testGroup.getId();

        groupLikeService.toggleLikeGroup(groupId, memberId);

        long likeCount = groupLikeRepository.countByGroupIdAndMemberId(groupId, memberId);
        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("동일 유저가 같은 그룹에 대해 좋아요를 눌렀다가 취소했을 때, likeCount ＝ ０")
    void t2() throws Exception {
        Long memberId = testMembers.get(0).getId();
        Long groupId = testGroup.getId();

        groupLikeService.toggleLikeGroup(groupId, memberId);
        groupLikeService.toggleLikeGroup(groupId, memberId);

        long likeCount = groupLikeRepository.countByGroupIdAndMemberId(groupId, memberId);
        assertThat(likeCount).isEqualTo(0);
    }

    @Test
    @DisplayName("동일 유저가 같은 그룹에 대해 동시에 좋아요를 눌렀을 때, likeCount ＝ ０")
    void t3() throws Exception {
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Long memberId = testMembers.get(0).getId(); // 같은 멤버
        Long groupId = testGroup.getId(); // 같은 그룹

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    groupLikeService.toggleLikeGroup(groupId, memberId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 한 개만 저장되었는지 확인
        long likeCount = groupLikeRepository.countByGroupIdAndMemberId(groupId, memberId);
        assertThat(likeCount).isEqualTo(0L);
    }

    @Test
    @DisplayName("한 그룹에 100명의 유저가 동시에 좋아요를 눌렀을 때, 100개 저장")
    void t4() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (Member member : testMembers) {
            executorService.execute(() -> {
                try {
                    groupLikeService.toggleLikeGroup(testGroup.getId(), member.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // 저장된 좋아요 개수 확인
        long likeCount = groupLikeRepository.count();
        assertThat(likeCount).isEqualTo(100);
    }
}


