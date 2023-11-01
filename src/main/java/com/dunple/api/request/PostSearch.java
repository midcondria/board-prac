package com.dunple.api.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostSearch {

    private static final int MAX_SIZE = 2000;
    private static final int MIN_PAGE = 1;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

    public long getOffset() {
        return (long) (Math.max(MIN_PAGE, page) - 1) * MAX_SIZE;
    }
}
