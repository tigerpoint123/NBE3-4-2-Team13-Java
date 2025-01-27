package com.app.backend.domain.group.supporter;

import com.app.backend.domain.group.controller.GroupController;
import com.app.backend.domain.group.service.GroupService;
import com.app.backend.global.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(GroupController.class)
public abstract class WebMvcTestSupporter {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected GroupService groupService;

}
