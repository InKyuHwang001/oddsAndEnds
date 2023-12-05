package com.oddsandends.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oddsandends.api.domain.Member;
import com.oddsandends.api.repository.MemberRepository;
import com.oddsandends.api.request.Login;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void clean() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 성공")
    void login1() throws Exception {
        // given
        memberRepository.save(Member.builder()
                .name("황인규")
                .email("hwanginkyu42@naver.com")
                .password("1234")
                .build());

        Login login = Login.builder()
                .email("hwanginkyu42@naver.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    @DisplayName("로그인 성공후 세션 1개 생성")
    void test2() throws Exception {
        // given
        Member user = memberRepository.save(Member.builder()
                .name("황인규")
                .email("hwanginkyu42@naver.com")
                .password("1234")
                .build());

        Login login = Login.builder()
                .email("hwanginkyu42@naver.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L, user.getSessions().size());
    }

    @Test
    @DisplayName("로그인 성공후 세션 응답")
    void login3() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder()
                .name("황인규")
                .email("hwanginkyu42@naver.com")
                .password("1234")
                .build());

        Login login = Login.builder()
                .email("hwanginkyu42@naver.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andDo(print());
    }

   @Test
   @DisplayName("로그인 후 권한이 필요한 페이접속")
   void login4() throws Exception{

   }
}