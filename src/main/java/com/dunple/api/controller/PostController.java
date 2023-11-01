package com.dunple.api.controller;

import com.dunple.api.config.data.UserSession;
import com.dunple.api.request.PostCreate;
import com.dunple.api.request.PostEdit;
import com.dunple.api.request.PostSearch;
import com.dunple.api.response.PostResponse;
import com.dunple.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/hello")
    public String hello(UserSession userSession) {
        return userSession.getName();
    }

    @PostMapping("/posts")
    public PostResponse post(@RequestBody @Valid PostCreate request) {
        request.validate();
        return postService.write(request);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long id) {
        return postService.get(id);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(Pageable pageable) {
        ResponseEntity.ok("a");
        return postService.getList(pageable);
    }

    @GetMapping("/posts2")
    public List<PostResponse> getListQueryDsl(PostSearch postSearch) {
        return postService.getListQueryDsl(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public PostResponse edit(@PathVariable Long postId, @RequestBody @Valid PostEdit request) {
        return postService.edit(postId, request);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }
}
