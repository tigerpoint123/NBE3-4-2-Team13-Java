package com.app.backend.domain.meetingApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.domain.meetingApplication.entity.MeetingApplication;

public interface MeetingApplicationRepository extends JpaRepository<MeetingApplication, Long> {
}
