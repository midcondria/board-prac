package com.dunple.api.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob // java 에선 String 이지만 DB 에선 long text 형태로 생성되게 해줌
    private String content;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public PostEditor.PostEditorBuilder toEditor() {
        return PostEditor.builder()
            .title(title)
            .content(content);
    }

    public void edit(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void edit1(PostEditor postEditor) {
        title = postEditor.getTitle();
        content = postEditor.getContent();
    }
}


