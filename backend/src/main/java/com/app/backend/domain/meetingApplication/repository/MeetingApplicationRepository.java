package com.app.backend.domain.meetingApplication.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.domain.meetingApplication.entity.MeetingApplication;

public interface MeetingApplicationRepository extends JpaRepository<MeetingApplication, Long> {
	List<MeetingApplication> findByGroupId(Long id);

	List<MeetingApplication> findByGroupIdAndDisabled(Long groupId, boolean disabled);

	Optional<MeetingApplication> findByIdAndDisabled(Long meetingApplicationId, boolean disabled);
}
