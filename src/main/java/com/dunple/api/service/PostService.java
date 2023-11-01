package com.dunple.api.service;

import com.dunple.api.domain.Post;
import com.dunple.api.domain.PostEditor;
import com.dunple.api.exception.PostNotFoundException;
import com.dunple.api.repository.PostRepository;
import com.dunple.api.request.PostCreate;
import com.dunple.api.request.PostEdit;
import com.dunple.api.request.PostSearch;
import com.dunple.api.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostResponse write(PostCreate request) {
        Post post = Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .build();
        postRepository.save(post);
        return new PostResponse(post);
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
            () -> new PostNotFoundException()
        );
        return new PostResponse(post);
    }

    public List<PostResponse> getList(Pageable pageable) {
        // pageable 정도는 알아야한다.
        return postRepository.findAll(pageable).stream()
            .map(post -> new PostResponse(post))
            .collect(Collectors.toList());
    }

    public List<PostResponse> getListQueryDsl(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
            .map(post -> new PostResponse(post))
            .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse edit(Long id, PostEdit request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException());

        post.edit(request.getTitle(), request.getContent());
        return new PostResponse(post);
    }

    @Transactional
    public PostResponse edit1(Long id, PostEdit request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException());

        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();
        PostEditor postEditor = editorBuilder
            .title(request.getTitle())
            .content(request.getContent())
            .build();

        post.edit1(postEditor);

        return new PostResponse(post);
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException());

        postRepository.delete(post);
    }
}
