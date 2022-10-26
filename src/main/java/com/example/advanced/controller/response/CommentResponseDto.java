package com.example.advanced.controller.response;

import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String content;
//    private Member member;
//    private Post post;
    private Long memberId;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
