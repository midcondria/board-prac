package com.dunple.api.service;

import com.dunple.api.domain.Post;
import com.dunple.api.exception.PostNotFoundException;
import com.dunple.api.repository.PostRepository;
import com.dunple.api.request.PostCreate;
import com.dunple.api.request.PostEdit;
import com.dunple.api.request.PostSearch;
import com.dunple.api.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @DisplayName("글 작성")
    @Test
    void write() {
        // given
        PostCreate postCreate = PostCreate.builder()
            .title("글 제목입니다")
            .content("글 내용입니다 하하")
            .build();

        // when
        postService.write(postCreate);
        Post post = postRepository.findAll().get(0);

        // then
        assertEquals(1L, postRepository.count());
        assertEquals("글 제목입니다", post.getTitle());
        assertEquals("글 내용입니다 하하", post.getContent());
    }

    @DisplayName("글 단건 조회 성공")
    @Test
    void get() {
        // given
        Post requestPost = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(requestPost);

        // 클라이언트 요구사항
        // json 응답 title 값을 10글자로 제한해주세요

        // when
        PostResponse response = postService.get(requestPost.getId());

        // then
        assertNotNull(response);
        assertEquals("우리 이쁜 미카공주님", response.getTitle());
        assertEquals("우리 공주님", response.getContent());
    }

    @DisplayName("글 단건 조회 실패 - 존재하지 않는 글")
    @Test
    void get1() {
        // expected
        assertThrows(
            PostNotFoundException.class,
            () -> postService.get(1000L)
        );
    }

    @DisplayName("글 1페이지 오름차순 조회")
    @Test
    void getListAsc() {
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

        // sql -> select, limit, offset 은 기본으로 알아야함
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id"));

        // when
        List<PostResponse> posts = postService.getList(pageable);

        // then
        assertEquals(5L, posts.size());
        assertEquals("우리 이쁜 미카공주님", posts.get(0).getTitle());
        assertEquals("미카 공주님 찬양 4", posts.get(4).getTitle());
    }

    @DisplayName("글 1페이지 내림차순 조회")
    @Test
    void getListDesc() {
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

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));


        // when
        List<PostResponse> posts = postService.getList(pageable);

        // then
        assertEquals(5L, posts.size());
        assertEquals("짤녀 공주면 자러감", posts.get(0).getTitle());
        assertEquals("미카 공주님 찬양 27", posts.get(4).getTitle());
    }

    @DisplayName("글 1페이지 내림차순 조회 QueryDsl")
    @Test
    void getListQueryDslDesc() {
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

        PostSearch postSearch = PostSearch.builder()
            .size(10)
            .build();
        // when
        List<PostResponse> posts = postService.getListQueryDsl(postSearch);

        // then
        assertEquals(10L, posts.size());
        assertEquals("짤녀 공주면 자러감", posts.get(0).getTitle());
        assertEquals("미카 공주님 찬양 27", posts.get(4).getTitle());
        assertEquals("미카 공주님 찬양 22", posts.get(9).getTitle());
    }

    @DisplayName("글 제목 수정 성공")
    @Test
    void edit() {
        // given
        Post post = Post.builder()
            .title("우리 이쁜 미카공주님")
            .content("우리 공주님")
            .build();
        postRepository.save(post);

        PostEdit request = PostEdit.builder()
            .title("사랑스러운 공주님")
            .content("우리 공주님")  // 이 부분은 client 와의 상의가 필요
            .build();

        // when
        postService.edit(post.getId(), request);
        Post changedPost = postRepository.findById(post.getId())
            .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));

        // then
        assertEquals(1L, postRepository.count());
        assertEquals("사랑스러운 공주님", changedPost.getTitle());
        assertEquals("우리 공주님", changedPost.getContent());
    }

    @DisplayName("글 제목 수정 실패 - 존재하지 않는 글")
    @Test
    void edit1() {
        // given
        PostEdit request = PostEdit.builder()
            .title("사랑스러운 공주님")
            .content("우리 공주님")
            .build();

        // expected
        assertThrows(
            PostNotFoundException.class,
            () -> postService.edit(1000L, request)
        );
    }

    @DisplayName("글 내용 수정 성공")
    @Test
    void edit2() {
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

        // when
        postService.edit(post.getId(), request);
        Post changedPost = postRepository.findById(post.getId())
            .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));

        // then
        assertEquals(1L, postRepository.count());
        assertEquals("우리 이쁜 미카공주님", changedPost.getTitle());
        assertEquals("이쁜 겅두님", changedPost.getContent());
    }

    @DisplayName("글 내용 수정 실패 - 존재하지 않는 글")
    @Test
    void edit3() {
        // given
        PostEdit request = PostEdit.builder()
            .title("우리 이쁜 미카공주님")
            .content("이쁜 겅두님")
            .build();

        // expected
        assertThrows(
            PostNotFoundException.class,
            () -> postService.edit(1000L, request)
        );
    }

    @DisplayName("게시글 삭제 성공")
    @Test
    void delete() {
        // given
        Post post = Post.builder()
            .title("미카는 고릴라")
            .content("우호우호")
            .build();
        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertEquals(0, postRepository.count());
    }

    @DisplayName("게시글 삭제 실패 - 존재하지 않는 글")
    @Test
    void delete1() {
        // given
        Post post = Post.builder()
            .title("미카는 고릴라")
            .content("우호우호")
            .build();
        postRepository.save(post);

        // expected
        assertThrows(
            PostNotFoundException.class,
            () -> postService.delete(post.getId() + 1000)
        );
    }

//    @DisplayName("")
//    @Test
//    public static String statement(Invoice invoice, Play[] plays) throws Exception {
//        int totalAmount = 0;
//        int volumeCredits = 0;
//        String result = String.format("청구 내역 (고객명: %s)\n", invoice.getCustomer());
//
//        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en-US"));
//        format.setCurrency(Currency.getInstance("USD"));
//        format.setMinimumFractionDigits(2);
//
//        for (Performance perf : invoice.getPerformances()) {
//            Play play =
//                Arrays.stream(plays)
//                    .filter(p -> p.getPlayId().equals(perf.getPlayId()))
//                    .findFirst()
//                    .get();
//
//            int thisAmount = 0;
//
//            switch (play.getType()) {
//                case "tragedy":
//                    thisAmount = 40000;
//                    if (perf.getAudience() > 30) {
//                        thisAmount += 1000 * (perf.getAudience() - 30);
//                    }
//                    break;
//                case "comedy":
//                    thisAmount = 30000;
//                    if (perf.getAudience() > 20) {
//                        thisAmount += 10000 + 500 * (perf.getAudience() - 20);
//                    }
//                    thisAmount += 300 * perf.getAudience();
//                    break;
//                default:
//                    throw new Exception(String.format("알 수 없는 장르: %s", play.getType()));
//            }
//            // 포인트 적립
//            volumeCredits += Math.max(perf.getAudience() - 30, 0);
//
//            // 희극 관객 5명마다 추가 포인트 제공
//            if (play.getType().equals("comedy")) {
//                volumeCredits += Math.floor(perf.getAudience() / 5);
//            }
//
//            // 청구 내역 출력
//            result +=
//                String.format(
//                    "%15s:%12s%4s석\n",
//                    play.getName(), format.format(thisAmount / 100), perf.getAudience());
//            totalAmount += thisAmount;
//        }
//
//        result += String.format("총액: %s\n", format.format(totalAmount / 100));
//        result += String.format("적립 포인트: %s점\n", volumeCredits);
//        return result;
//    }
}

