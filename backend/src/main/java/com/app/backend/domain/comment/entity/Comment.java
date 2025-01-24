package com.app.backend.domain.comment.entity;

import com.app.backend.global.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_comments")
public class Comment extends BaseEntity {

	@Id
	@Column(name = "comment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(nullable = false)
	private String content;



	//추후 수정

	//@ManyToOne
	//@JoinColumn(name = "post_id")
	//private Long postId;

	//@ManyToOne
	//@JoinColumn(name = "member_id")
	//private Long membersId;



}
