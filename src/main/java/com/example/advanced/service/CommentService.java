package com.example.advanced.service;

import com.example.advanced.controller.handler.CustomError;
import com.example.advanced.controller.request.CommentRequestDto;
import com.example.advanced.controller.request.PostRequestDto;
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


    // 댓글 조회
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
                            .memberId(comment.getMember().getMemberId())
                            .postId(comment.getPost().getPostId())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(commentResponseDtoList);
    }


    // 댓글 작성 -> 댓글 내용에 대한 응답 해주기
    @Transactional
    public ResponseDto<?> createComment(CommentRequestDto commentRequestDto, HttpServletRequest request) {
        Member member = validateMember(request);

        if (null == member) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
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

        return ResponseDto.success(null);

    }

    // 댓글 업데이트
    @Transactional
    public ResponseDto<?> updateComment(Long commentId, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        Member member = validateMember(request);

        if (null == member) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
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

    // 댓글 삭제
    @Transactional
    public ResponseDto<?> deleteComment(Long commentId, HttpServletRequest request) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
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
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


}
