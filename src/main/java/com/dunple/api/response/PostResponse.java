package com.dunple.api.response;

import com.dunple.api.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponse {

    private final long id;
    private final String title;
    private final String content;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle().substring(0, Math.min(post.getTitle().length(), 12));
        this.content = post.getContent();
    }

    @Builder
    public PostResponse(long id, String title, String content) {
        this.id = id;
        this.title = title.substring(0, Math.min(title.length(), 12));
        this.content = content;
    }
}
