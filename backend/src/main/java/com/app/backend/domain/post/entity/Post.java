package com.app.backend.domain.post.entity;

import com.app.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(name = "tbl_posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    // Domain 별 Entity 추가 작업 완료 후 주석 해제

//    @JoinColumn(name = "member_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Member member;
//
//    @JoinColumn(name = "group_id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Group group;
//
//    @OneToMany(mappedBy = "post")
//    private List<Comment> comments = new ArrayList<>();

}
