package com.dunple.api.controller;

import com.dunple.api.domain.Post;
import com.dunple.api.repository.PostRepository;
import com.dunple.api.request.PostCreate;
import com.dunple.api.request.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @DisplayName("/hello 요청시 Hello World 를 출력한다.")
    @Test
    void hello() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/hello")
                .header("Authorization","midcon")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(""))
            .andDo(print());
    }

    @DisplayName("글 작성 시 저장한 Post 를 출력한다.")
    @Test
    void post1() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("글 제목입니다")
            .content("글 내용입니다 하하")
            .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/posts")
                    .contentType(APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("글 제목입니다"))
            .andExpect(jsonPath("$.content").value("글 내용입니다 하하"))
            .andDo(print());
    }

    @DisplayName("글 작성 시 title 값은 필수다.")
    @Test
    void post2() throws Exception {
        // given
        Post build = Post.builder()
            .build();
        System.out.println("builder = " + build);

        PostCreate request = PostCreate.builder()
            .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/posts")
                    .contentType(APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
            .andExpect(jsonPath("$.validation.title").value("제목을 입력해주세요."))
            .andExpect(jsonPath("$.validation.content").value("내용을 입력해주세요."))
            .andDo(print());
    }

    @DisplayName("글 작성 시 DB에 값이 저장된다.")
    @Test
    void post3() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("글 제목입니다")
            .content("글 내용입니다 하하")
            .build();

        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(
                post("/posts")
                    .contentType(APPLICATION_JSON)
                    .content(json)
            )
            .andExpect(status().isOk())
            .andDo(print());
        Post post = postRepository.findAll().get(0);

        // then
        assertEquals(1L, postRepository.count());
        assertEquals("글 제목입니다", post.getTitle());
        assertEquals("글 내용입니다 하하", post.getContent());
    }

    @DisplayName("글 작성 시 제목에 '바보'는 포함될 수 없다.")
    @Test
    void post4() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("미카 공주님은 바보")
            .content("우리 공주님")
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(
                post("/posts")
                    .contentType(APPLICATION_JSON)
                    .content(json)
            )
            .andDo(print())
            .andExpect(status().isBadRequest());

        assertEquals(0L, postRepository.count());
    }

    @DisplayName("글 단건 조회 성공")
    @Test
    void get() throws Exception {
        // given
        Post post = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.get("/posts/{postId}",post.getId())
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(post.getId()))
            .andExpect(jsonPath("$.title").value("우리 이쁜 미카공주님"))
            .andExpect(jsonPath("$.content").value("우리 공주님"))
            .andDo(print());
    }

    @DisplayName("글 단건 조회 실패 - 존재하지 않는 글")
    @Test
    void get1() throws Exception {
        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.get("/posts/{postId}",1000L)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @DisplayName("글 단건 조회시 JSON 응답에 제목은 12글자 까지만 제한")
    @Test
    void get2() throws Exception {
        // given
        Post post = Post.builder()
            .title("123456789012345")
            .content("우리 공주님")
            .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.get("/posts/{postId}", post.getId())
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(post.getId()))
            .andExpect(jsonPath("$.title").value("123456789012"))
            .andExpect(jsonPath("$.content").value("우리 공주님"))
            .andDo(print());
    }

    @DisplayName("글 1페이지 오름차순 조회")
    @Test
    void getListAsc() throws Exception {
        // given
        Post post1 = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post1);

        List<Post> requestPosts = IntStream.rangeClosed(1, 30)
            .mapToObj(i -> {
                return Post.builder()
                    .title("미카 공주님 찬양 " + i)
                    .content("찬양내용 " + i)
                    .build();
            })
            .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        Post post2 = Post.builder()
            .title("짤녀 공주면 자러감")
            .content("잘 자")
            .build();
        postRepository.save(post2);

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.get("/posts?page=1&sort=id")
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", Matchers.is(5)))
//            .andExpect(jsonPath("$[0].id", Matchers.is(1)))
            .andExpect(jsonPath("$[0].title", Matchers.is("우리 이쁜 미카공주님")))
            .andExpect(jsonPath("$[0].content", Matchers.is("우리 공주님")))
//            .andExpect(jsonPath("$[4].id", Matchers.is(5)))
            .andExpect(jsonPath("$[4].title", Matchers.is("미카 공주님 찬양 4")))
            .andExpect(jsonPath("$[4].content", Matchers.is("찬양내용 4")))
            .andDo(print());
    }

    @DisplayName("글 1페이지 내림차순 조회")
    @Test
    void getListDesc() throws Exception {
        // given
        Post post1 = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post1);

        List<Post> requestPosts = IntStream.rangeClosed(1, 30)
            .mapToObj(i -> {
                return Post.builder()
                    .title("미카 공주님 찬양 " + i)
                    .content("찬양내용 " + i)
                    .build();
            })
            .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        Post post2 = Post.builder()
            .title("짤녀 공주면 자러감")
            .content("잘 자")
            .build();
        postRepository.save(post2);

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.get("/posts?page=1&sort=id,desc")
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", Matchers.is(5)))
//            .andExpect(jsonPath("$[0].id", Matchers.is(32)))
            .andExpect(jsonPath("$[0].title", Matchers.is("짤녀 공주면 자러감")))
            .andExpect(jsonPath("$[0].content", Matchers.is("잘 자")))
//            .andExpect(jsonPath("$[4].id", Matchers.is(28)))
            .andExpect(jsonPath("$[4].title", Matchers.is("미카 공주님 찬양 27")))
            .andExpect(jsonPath("$[4].content", Matchers.is("찬양내용 27")))
            .andDo(print());
    }

    @DisplayName("글 1페이지 내림차순 조회 QueryDsl")
    @Test
    void getListQueryDsl() throws Exception {
        // given
        Post post1 = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post1);

        List<Post> requestPosts = IntStream.rangeClosed(1, 30)
            .mapToObj(i -> {
                return Post.builder()
                    .title("미카 공주님 찬양 " + i)
                    .content("찬양내용 " + i)
                    .build();
            })
            .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        Post post2 = Post.builder()
            .title("짤녀 공주면 자러감")
            .content("잘 자")
            .build();
        postRepository.save(post2);

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.get("/posts2?page=1&size=10")
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", Matchers.is(10)))
            .andExpect(jsonPath("$[0].title", Matchers.is("짤녀 공주면 자러감")))
            .andExpect(jsonPath("$[0].content", Matchers.is("잘 자")))
            .andExpect(jsonPath("$[4].title", Matchers.is("미카 공주님 찬양 27")))
            .andExpect(jsonPath("$[4].content", Matchers.is("찬양내용 27")))
            .andDo(print());
    }

    @DisplayName("글 제목 수정")
    @Test
    void edit() throws Exception {
        // given
        Post post = Post.builder()
            .title("우리 이쁜 미카골주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post);

        PostEdit request = PostEdit.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")    // 이 부분은 client 와의 상의가 필요
            .build();

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/posts/{postId}", post.getId())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("우리 이쁜 미카공주님"))
            .andExpect(jsonPath("$.content").value("우리 공주님"))
            .andDo(print());
    }

    @DisplayName("글 제목 수정 실패 - 존재하지 않는 글")
    @Test
    void edit1() throws Exception {
        // given
        PostEdit request = PostEdit.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/posts/{postId}", 1000L)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @DisplayName("글 내용 수정")
    @Test
    void edit2() throws Exception {
        // given
        Post post = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post);

        PostEdit request = PostEdit.builder()
            .title("우리 이쁜 미카공주님")
            .content("이쁜 겅두님")
            .build();

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/posts/{postId}", post.getId())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("우리 이쁜 미카공주님"))
            .andExpect(jsonPath("$.content").value("이쁜 겅두님"))
            .andDo(print());
    }

    @DisplayName("글 내용 수정 실패 - 존재하지 않는 글")
    @Test
    void edit3() throws Exception {
        // given
        PostEdit request = PostEdit.builder()
            .title("우리 이쁜 미카공주님")
            .content("이쁜 겅두님")
            .build();

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/posts/{postId}", 1000L)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @DisplayName("글 삭제 성공")
    @Test
    void delete() throws Exception {
        // given
        Post post = Post.builder()
            .title("미카는 고릴라")
            .content("우호우호")
            .build();
        postRepository.save(post);

        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/posts/{postId}", post.getId())
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andDo(print());
    }

    @DisplayName("글 삭제 실패 - 존재하지 않는 글")
    @Test
    void delete1() throws Exception {
        // expected
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/posts/{postId}", 1000L)
                    .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andDo(print());
    }
}


