package com.example.advanced.service;

import com.example.advanced.controller.handler.CustomError;
import com.example.advanced.controller.request.CommentRequestDto;
import com.example.advanced.controller.response.CommentResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.Comment;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final TokenProvider tokenProvider;


    // ๋๊ธ ์กฐํ
    @Transactional(readOnly = true)
    public ResponseDto<?> getComment(Long postId, Pageable pageable) {
        Post post = postService.isPresentPost(postId);

        if(post == null) {
            return ResponseDto.fail(CustomError.POST_NOT_FOUND.name(),
                    CustomError.POST_NOT_FOUND.getMessage());
        }

        Page<Comment> commentList = commentRepository.findByPost(post, pageable);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for(Comment comment : commentList){
            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .commentId(comment.getCommentId())
                            .content(comment.getContent())
                            .nickname(comment.getMember().getNickname())
                            .memberId(comment.getMember().getMemberId())
                            .postId(comment.getPost().getPostId())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(commentResponseDtoList);
    }


    // ๋๊ธ ์์ฑ -> ๋๊ธ ๋ด์ฉ์ ๋ํ ์๋ต ํด์ฃผ๊ธฐ
    @Transactional
    public ResponseDto<?> createComment(CommentRequestDto commentRequestDto, HttpServletRequest request) {
        Member member = validateMember(request);

        if (null == member) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }

        if (null == request.getHeader("refresh_token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        Post post = postService.isPresentPost(commentRequestDto.getPostId());
        if (null == post) {
            return ResponseDto.fail(CustomError.POST_NOT_FOUND.name(),
                    CustomError.POST_NOT_FOUND.getMessage());
        }

        Comment comment = Comment.builder()
                .content(commentRequestDto.getContent())
                .member(member)
                .post(post)
                .build();

        commentRepository.save(comment);

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .memberId(comment.getMember().getMemberId())
                .postId(comment.getPost().getPostId())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();

        return ResponseDto.success(commentResponseDto);

    }

    // ๋๊ธ ์๋ฐ์ดํธ
    @Transactional
    public ResponseDto<?> updateComment(Long commentId, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        Member member = validateMember(request);

        if (null == member) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }

        if (null == request.getHeader("refresh_token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        Post post = postService.isPresentPost(commentRequestDto.getPostId());
        if (null == post) {
            return ResponseDto.fail(CustomError.POST_NOT_FOUND.name(),
                    CustomError.POST_NOT_FOUND.getMessage());
        }

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail(CustomError.COMMENT_NOT_FOUND.name(),
                    CustomError.COMMENT_NOT_FOUND.getMessage());
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(),
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }

        comment.update(commentRequestDto);

        return ResponseDto.success(null);
    }

    // ๋๊ธ ์ญ์?
    @Transactional
    public ResponseDto<?> deleteComment(Long commentId, HttpServletRequest request) {
        Member member = validateMember(request);

        if (null == member) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }

        if (null == request.getHeader("refresh_token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail(CustomError.COMMENT_NOT_FOUND.name(),
                    CustomError.COMMENT_NOT_FOUND.getMessage());
        }

        if (comment.validateMember(member)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(),
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }

        commentRepository.delete(comment);
        return ResponseDto.success("success");

    }


    @Transactional(readOnly = true)
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("refresh_token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


}
