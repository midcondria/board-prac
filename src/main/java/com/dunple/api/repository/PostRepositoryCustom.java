package com.dunple.api.repository;

import com.dunple.api.domain.Post;
import com.dunple.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
