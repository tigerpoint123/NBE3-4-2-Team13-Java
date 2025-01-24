package com.app.backend.domain.group.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.app.backend.domain.group.entity.Group;
import com.app.backend.domain.group.entity.RecruitStatus;
import com.app.backend.global.config.QuerydslConfig;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageName : com.app.backend.domain.group.repository
 * FileName    : GroupRepositoryTest
 * Author      : loadingKKamo21
 * Date        : 25. 1. 24.
 * Description :
 */
@ActiveProfiles("test")
@Import(QuerydslConfig.class)
@DataJpaTest
@Transactional
class GroupRepositoryTest {

    @Autowired
    private GroupRepository   groupRepository;
    @Autowired
    private TestEntityManager em;

    @AfterEach
    void afterEach() {
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("save")
    void save() {
        //Given
        Group group = Group.of("test", "test province", "test city", "test town", "test description",
                               RecruitStatus.RECRUITING, 10);

        //When
        Group savedGroup = groupRepository.save(group);
        afterEach();

        //Then
        Long  id        = savedGroup.getId();
        Group findGroup = em.find(Group.class, id);

        assertThat(findGroup.getId()).isEqualTo(id);
        assertThat(findGroup.getName()).isEqualTo(group.getName());
        assertThat(findGroup.getProvince()).isEqualTo(group.getProvince());
        assertThat(findGroup.getCity()).isEqualTo(group.getCity());
        assertThat(findGroup.getTown()).isEqualTo(group.getTown());
        assertThat(findGroup.getDescription()).isEqualTo(group.getDescription());
        assertThat(findGroup.getRecruitStatus()).isEqualTo(group.getRecruitStatus());
        assertThat(findGroup.getMaxRecruitCount()).isEqualTo(group.getMaxRecruitCount());
    }

    @Test
    @DisplayName("save, field is null")
    void save_nullCheck() {
        //Given
        String name = null;

        //When

        //Then
        assertThatThrownBy(() -> Group.of(name, "test province", "test city", "test town", "test description",
                                          RecruitStatus.RECRUITING, 10))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("findById")
    void findById() {
        //Given
        Group group = Group.of("test", "test province", "test city", "test town", "test description",
                               RecruitStatus.RECRUITING, 10);
        Group savedGroup = em.persist(group);
        Long  id         = savedGroup.getId();
        afterEach();

        //When
        Optional<Group> findGroup = groupRepository.findById(id);

        //Then
        assertThat(findGroup).isPresent();
        assertThat(findGroup.get().getId()).isEqualTo(id);
        assertThat(findGroup.get().getName()).isEqualTo(group.getName());
        assertThat(findGroup.get().getProvince()).isEqualTo(group.getProvince());
        assertThat(findGroup.get().getCity()).isEqualTo(group.getCity());
        assertThat(findGroup.get().getTown()).isEqualTo(group.getTown());
        assertThat(findGroup.get().getDescription()).isEqualTo(group.getDescription());
        assertThat(findGroup.get().getRecruitStatus()).isEqualTo(group.getRecruitStatus());
        assertThat(findGroup.get().getMaxRecruitCount()).isEqualTo(group.getMaxRecruitCount());
    }

    @Test
    @DisplayName("findById, unknown id")
    void findById_unknownId() {
        //Given
        Long unknownId = 1234567890L;

        //When
        Optional<Group> findGroup = groupRepository.findById(unknownId);

        //Then
        assertThat(findGroup).isNotPresent();
    }

    @Test
    @DisplayName("update")
    void update() {
        //Given
        Group group = Group.of("test", "test province", "test city", "test town", "test description",
                               RecruitStatus.RECRUITING, 10);
        Group savedGroup = em.persist(group);
        Long  id         = savedGroup.getId();
        afterEach();

        //When
        groupRepository.findById(id).ifPresent(g -> {
            g.modifyName("new test");
            g.modifyRegion("new test province", "new test city", "new test town");
            g.modifyDescription("new test description");
            g.modifyRecruitStatus(RecruitStatus.CLOSED);
            g.modifyMaxRecruitCount(20);
        });
        afterEach();

        //Then
        Group findGroup = em.find(Group.class, id);

        assertThat(findGroup.getId()).isEqualTo(id);
        assertThat(findGroup.getName()).isNotEqualTo(group.getName());
        assertThat(findGroup.getName()).isEqualTo("new test");
        assertThat(findGroup.getProvince()).isNotEqualTo(group.getProvince());
        assertThat(findGroup.getProvince()).isEqualTo("new test province");
        assertThat(findGroup.getCity()).isNotEqualTo(group.getCity());
        assertThat(findGroup.getCity()).isEqualTo("new test city");
        assertThat(findGroup.getTown()).isNotEqualTo(group.getTown());
        assertThat(findGroup.getTown()).isEqualTo("new test town");
        assertThat(findGroup.getDescription()).isNotEqualTo(group.getDescription());
        assertThat(findGroup.getDescription()).isEqualTo("new test description");
        assertThat(findGroup.getRecruitStatus()).isNotEqualTo(group.getRecruitStatus());
        assertThat(findGroup.getRecruitStatus()).isEqualTo(RecruitStatus.CLOSED);
        assertThat(findGroup.getMaxRecruitCount()).isNotEqualTo(group.getMaxRecruitCount());
        assertThat(findGroup.getMaxRecruitCount()).isEqualTo(20);
    }

    @Test
    @DisplayName("deleteById")
    void deleteById() {
        //Given
        Group group = Group.of("test", "test province", "test city", "test town", "test description",
                               RecruitStatus.RECRUITING, 10);
        Group savedGroup = em.persist(group);
        Long  id         = savedGroup.getId();
        afterEach();

        //When
        Optional<Group> opGroup = groupRepository.findById(id);

        //Then
        assertThat(opGroup).isEmpty();
    }

}