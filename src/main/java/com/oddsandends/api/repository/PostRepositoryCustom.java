package com.oddsandends.api.repository;

import com.oddsandends.api.domain.Post;
import com.oddsandends.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);

}
