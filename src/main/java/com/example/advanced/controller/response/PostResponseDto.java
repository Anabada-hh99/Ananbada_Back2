package com.example.advanced.controller.response;

import com.example.advanced.domain.Member;
import com.example.advanced.domain.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String nickname;
    private String title;
    private String content;
    private Long price;
    private String imageUrl;
//    private List<CommentResponseDto> commentResponseDtoList;
    private Integer count;
    private PostCategory category;
    private Boolean state;
    private LocalDateTime modifiedAt;
    private Long memberId;

}
