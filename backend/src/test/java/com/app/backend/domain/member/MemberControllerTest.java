package com.app.backend.domain.member;

import com.app.backend.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest           // 전체 애플리케이션 컨텍스트 로드
@AutoConfigureMockMvc     // MockMvc 자동 구성
@Transactional           // 테스트 후 롤백
public class MemberControllerTest {
    @Autowired
    public MemberService memberService;
    @Autowired
    private MockMvc mvc;


}
