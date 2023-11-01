package com.dunple.api.repository;

import com.dunple.api.domain.Post;
import com.dunple.api.request.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.dunple.api.domain.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
            .limit(postSearch.getSize())
            .offset(postSearch.getOffset())
            .orderBy(post.id.desc())
            .fetch();
    }
}
