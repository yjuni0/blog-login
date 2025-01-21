package com.project.blog.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchDto {
    private String title;
    private String content;
    private String writer;

    @Builder
    public SearchDto(String title, String content, String writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static SearchDto createSearchData(String title, String content, String writer) {
        return SearchDto.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .build();
    }
}
