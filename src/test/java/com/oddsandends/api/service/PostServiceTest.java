package com.oddsandends.api.service;

import com.oddsandends.api.domain.Post;
import com.oddsandends.api.exception.PostNotFound;
import com.oddsandends.api.repository.PostRepository;
import com.oddsandends.api.request.PostCreate;
import com.oddsandends.api.request.PostEdit;
import com.oddsandends.api.request.PostSearch;
import com.oddsandends.api.response.PostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;

    @BeforeEach()
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void write1() {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목")
                .content("내용")
                .build();

        //when
        postService.write(postCreate);

        //then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목", post.getTitle());
        assertEquals("내용", post.getContent());
    }

    @Test
    @DisplayName("글 단건 조회")
    void get1() {
        //given
        Post requestPost = Post.builder()
                .title("hw")
                .content("ik").build();
        postRepository.save(requestPost);

        //when
        PostResponse response = postService.get(requestPost.getId());

        //then
        assertNotNull(response);
        assertEquals("hw", response.getTitle());
        assertEquals("ik", response.getContent());
    }

    @Test
    @DisplayName("글 단건 조회 : 존재하지 않는 글")
    void get2() {
        //given
        Post requestPost = Post.builder()
                .title("hw")
                .content("ik").build();
        postRepository.save(requestPost);

        //expected
        PostNotFound e = assertThrows(PostNotFound.class, () -> {
            postService.get(requestPost.getId() + 1L);
        });

        assertEquals("존재하지 않는 글 입니다.", e.getMessage());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void getList() {
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

        PostSearch search = PostSearch.builder()
                .page(1)
                .build();

        //when
        List<PostResponse> postList = postService.getList(search);

        //then
        assertEquals(10L, postList.size());
        assertEquals("title - 30", postList.get(0).getTitle());
        assertEquals("content - 30", postList.get(0).getContent());
        assertEquals("title - 26", postList.get(4).getTitle());
        assertEquals("content - 26", postList.get(4).getContent());
    }

    @Test
    @DisplayName("글 제목 수정")
    void edit1() {
        //given

        Post post = Post.builder()
                .title("title - 1")
                .content("content - 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("title - 1r")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id =" + post.getId()));

        assertEquals("title - 1r", changedPost.getTitle());
        assertEquals("content - 1", changedPost.getContent());
    }


    @Test
    @DisplayName("글 내용 수정")
    void edit2() {
        //given

        Post post = Post.builder()
                .title("title - 1")
                .content("content - 1")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .content("content - 1r")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id =" + post.getId()));

        assertEquals("title - 1", changedPost.getTitle());
        assertEquals("content - 1r", changedPost.getContent());
    }

    @Test
    @DisplayName("글 수정 - 존재하지 않는 글")
    void edit3() {
        //given
        Post requestPost = Post.builder()
                .title("title - 1")
                .content("content - 1")
                .build();
        postRepository.save(requestPost);

        PostEdit postEdit = PostEdit.builder()
                .content("content - 1r")
                .build();
        //expected
        assertThrows(PostNotFound.class, () -> {
            postService.edit(requestPost.getId() + 1L, postEdit);
        });

    }

    @Test
    @DisplayName("글 삭제")
    void delete1() {
        //given
        Post post = Post.builder()
                .title("title - 1")
                .content("content - 1")
                .build();
        postRepository.save(post);

        //when
        postService.delete(post.getId());

        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 삭제 : 존재하지 않는 글")
    void delete2() {
        //given
        Post post = Post.builder()
                .title("title - 1")
                .content("content - 1")
                .build();
        postRepository.save(post);

        //expected
        Assertions.assertThrows(PostNotFound.class, ()-> {
            postService.delete(post.getId() + 1L);
        });
    }
}