package com.dunple.api.controller;

import com.dunple.api.domain.Post;
import com.dunple.api.repository.PostRepository;
import com.dunple.api.request.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.midcon.com", uriPort = 443)
public class PostControllerDocTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("글 단건 조회")
    @Test
    void test1() throws Exception {
        // given
        Post post = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post);

        mockMvc.perform(
                get("/posts/{postId}", 1L)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("post-inquiry",
                pathParameters(
                    parameterWithName("postId").description("게시글 ID")
                ),
                responseFields(
                    fieldWithPath("id").description("게시글 ID"),
                    fieldWithPath("title").description("글 제목"),
                    fieldWithPath("content").description("글 내용")
                )
            ));
    }

    @DisplayName("글 등록")
    @Test
    void test2() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(json)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("post-create",
                requestFields(
                    fieldWithPath("title").description("글 제목")
                        .attributes(Attributes.key("constraint").value("착한 제목 입력해주세요.")),
                    fieldWithPath("content").description("글 내용").optional()
                ),
                responseFields(
                    fieldWithPath("id").description("게시글 ID"),
                    fieldWithPath("title").description("글 제목"),
                    fieldWithPath("content").description("글 내용")
                )
            ));
    }
}
