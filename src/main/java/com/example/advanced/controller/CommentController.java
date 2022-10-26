package com.example.advanced.controller;

import com.example.advanced.controller.request.CommentRequestDto;
import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;


    // 댓글 조회
    @GetMapping("/comments/{postId}")
    public ResponseDto<?> getComment(@PageableDefault(sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable,
                                     @PathVariable Long postId) {
        return commentService.getComment(postId,pageable);
    }

    // 댓글 생성
    @PostMapping(value = "/comments")
    public ResponseDto<?> createComment(@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.createComment(commentRequestDto, request);
    }

    // 댓글 수정
    @PutMapping(value = "/comments/{commentId}")
    public ResponseDto<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto
                                        ,HttpServletRequest request) {
        return commentService.updateComment(commentId, commentRequestDto, request);
    }

    // 댓글 삭제
    @DeleteMapping(value = "/comments/{commentId}")
    public ResponseDto<?> deleteComment(@PathVariable Long commentId,HttpServletRequest request) {
        return commentService.deleteComment(commentId,request);
    }

}
