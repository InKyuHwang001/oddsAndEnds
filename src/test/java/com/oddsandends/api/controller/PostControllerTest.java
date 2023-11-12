package com.oddsandends.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oddsandends.api.domain.Post;
import com.oddsandends.api.repository.PostRepository;
import com.oddsandends.api.request.PostCreate;
import com.oddsandends.api.request.PostEdit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;


    @BeforeEach
    void cleanDBS() {
        postRepository.deleteAll();

    }

    @AfterEach
    void cleanDBE(){
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("posts 요청시 제목과 내용이 필수")
    void post1() throws Exception {
        //given
        PostCreate request = PostCreate
                .builder()
                .title("제목")
                .content("내용")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    @DisplayName("posts 요청시 제목이 \"\"인 경우")
    void post2() throws Exception {
        //given
        PostCreate request = PostCreate
                .builder()
                .title("")
                .content("내용")
                .build();


        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("title error"))
                .andDo(print());
    }

    @Test
    @DisplayName("posts 요청시 제목이 null 인 경우")
    void post3() throws Exception {
        //given
        PostCreate request = PostCreate
                .builder()
                .content("내용")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("title error"))
                .andDo(print());
    }

    @Test
    @DisplayName("posts 요청시 DB에 값이 저장")
    void post4() throws Exception {
        //given
        PostCreate request = PostCreate
                .builder()
                .title("제목")
                .content("내용")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목", post.getTitle());
        assertEquals("내용", post.getContent());
    }

    @Test
    @DisplayName("posts 요청시 DB에 값이 저장 : \"바보\"라는 글자는 포함할 수 없다.")
    void post5() throws Exception {
        //given
        PostCreate request = PostCreate
                .builder()
                .title("제목 바보")
                .content("내용")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    @Test
    @DisplayName("글 단건 조회")
    void get1() throws Exception {
        //given
        Post post = Post.builder()
                .title("hw")
                .content("ik").build();
        postRepository.save(post);

        //expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("글 단건 조회: 제목 10자 이하문")
    void get2() throws Exception {
        //given
        Post post = Post.builder()
                .title("012345678910")
                .content("ik").build();
        postRepository.save(post);

        //expected
        mockMvc.perform(get("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle().substring(0, 10)))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());

    }

    @Test
    @DisplayName("글 단건 조회 : 존재하지 않는 글")
    void get3() throws Exception {
        //given

        //expected
        mockMvc.perform(get("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @DisplayName("글 리스트 조회")
    void getList1() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> {
                    return Post.builder()
                            .title("title - " + i)
                            .content("content - " + i)
                            .build();
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        //expected
        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].id").value(requestPosts.get(29).getId()))
                .andExpect(jsonPath("$[0].content").value(requestPosts.get(29).getContent()))
                .andExpect(jsonPath("$[0].title").value(requestPosts.get(29).getTitle()))
                .andDo(print());

    }

    @Test
    @DisplayName("글 리스트 조회: 페이지가 0으로 들어오면 첫 페이지로")
    void getList2() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> {
                    return Post.builder()
                            .title("title - " + i)
                            .content("content - " + i)
                            .build();
                })
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        //expected
        mockMvc.perform(get("/posts?page=0&size=10")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].id").value(requestPosts.get(29).getId()))
                .andExpect(jsonPath("$[0].content").value(requestPosts.get(29).getContent()))
                .andExpect(jsonPath("$[0].title").value(requestPosts.get(29).getTitle()))
                .andDo(print());

    }


    @Test
    @DisplayName("글 수정")
    void edit1() throws Exception {
        //given

        Post post = Post.builder()
                .title("title - 1")
                .content("content - 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("title - 1r")
                .content("content - 1r")
                .build();

        //expected
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("글 수정 : 존재하지 않는 개시글")
    void edit2() throws Exception {
        //given

        PostEdit postEdit = PostEdit.builder()
                .title("title - 1r")
                .content("content - 1r")
                .build();

        //expected
        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @DisplayName("글 삭제")
    void delete1() throws Exception {
        //given
        Post post = Post.builder()
                .title("title - 1")
                .content("content - 1")
                .build();
        postRepository.save(post);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("글 삭제")
    void delete2() throws Exception {
        //given

        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

    }
}