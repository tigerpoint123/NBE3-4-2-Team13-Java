package com.app.backend.global.supporter;

import com.app.backend.domain.group.repository.GroupRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public abstract class SpringBootTestSupporter {

    @Autowired
    protected EntityManager em;

    @Autowired
    protected GroupRepository groupRepository;

}
