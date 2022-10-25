package com.example.advanced.service;


import com.example.advanced.controller.handler.CustomError;
import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.PostResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.Post;
import com.example.advanced.domain.PostCategory;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;

    private final TokenProvider tokenProvider;


    //게시글작성
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, HttpServletRequest request) {
        Member member = validateMember(request);

        if (null == member) {
            return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                    CustomError.INVALID_TOKEN.getMessage());
        }

        if (null == request.getHeader("Refresh_Token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        if (null == request.getHeader("Access_Token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }


        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .price(postRequestDto.getPrice())
                .imageUrl(postRequestDto.getImgUrl())
                .category(postRequestDto.getCategory())
                .state(true)
                .count(0)
                .member(member)
                .build();
        postRepository.save(post);
        return ResponseDto.success(true);

    }

    //게시글 수정
    @Transactional
    public ResponseDto<?> updatePost(Long postId, PostRequestDto postRequestDto, HttpServletRequest request) {

        Post post = isPresentPost(postId);

        Member member = validateMember(request);

        if (post.validateMember(member)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(),
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }


        if (null == request.getHeader("Refresh_Token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        if (null == request.getHeader("Access_Token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        post.update(postRequestDto);
        return ResponseDto.success(true);
    }

    //게시글 삭제
    @Transactional
    public ResponseDto<?> deletePost(Long postId, HttpServletRequest request) {

        Post post = isPresentPost(postId);

        Member member = validateMember(request);

        if (post.validateMember(member)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(),
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }


        if (null == request.getHeader("Refresh_Token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        if (null == request.getHeader("Access_Token")) {
            return ResponseDto.fail(CustomError.LOGINMEMBER_NOT_FOUND.name(),
                    CustomError.LOGINMEMBER_NOT_FOUND.getMessage());
        }

        postRepository.delete(post);
        return ResponseDto.success("delete success");
    }

    //게시글 상세조회

    @Transactional
    public ResponseDto<?> getPost(Long postId) {
        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail(CustomError.POST_NOT_FOUND.name(),
                    CustomError.POST_NOT_FOUND.getMessage());
        }

//        List<Comment> commentList = commentRepository.findAllByPost(post);
//        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//
//        for (Comment comment : commentList) {
//            commentResponseDtoList.add(
//                    CommentResponseDto.builder()
//                            .id(comment.getId())
//                            .author(comment.getMember().getNickname())
//                            .content(comment.getContent())
//                            .createdAt(comment.getCreatedAt())
//                            .modifiedAt(comment.getModifiedAt())
//                            .build()
//            );
//        }

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getPostId())
                        .nickname(post.getMember().getNickname())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imageUrl(post.getImageUrl())
                        .count(post.getCount())
                        .category(post.getCategory())
                        .count(updateView(postId))
//                       .commentResponseDtoList(commentResponseDtoList)
                        .state(post.getState())
                        .modifiedAt(post.getModifiedAt())
                        .memberId(post.getMember().getMemberId())
                        .build()
        );
    }

    //조횟수 증가
    @Transactional
    public int updateView(Long postId) {
        return postRepository.updateView(postId);
    }


    //물품판매상태 수정
    @Transactional
    public ResponseDto<?> checkStock(Long postId, PostRequestDto postRequestDto, HttpServletRequest request) {

        Member member = validateMember(request);

        Post post = isPresentPost(postId);

        if (post.validateMember(member)) {
            return ResponseDto.fail(CustomError.WRITER_NOT_MATCHED.name(),
                    CustomError.WRITER_NOT_MATCHED.getMessage());
        }


        post.updateState(postRequestDto.getState());

        return ResponseDto.success(true);

    }
    //게시글 전체 조회

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(Pageable pageable, Boolean isSaled) {

        // 매개 변수로 pagable을 넘기면 return형은 Page형이다.
        Page<Post> postList = postRepository.findAll(pageable);

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
            if (isSaled&&post.getState()) {
                postResponseDtoList.add(PostResponseDto.builder()
                        .id(post.getPostId())
                        .nickname(post.getMember().getNickname())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imageUrl(post.getImageUrl())
                        .count(post.getCount())
                        .category(post.getCategory())
                        .state(post.getState())
                        .modifiedAt(post.getModifiedAt())
                        .build()
                );
            } else if(!isSaled){
                postResponseDtoList.add(PostResponseDto.builder()
                        .id(post.getPostId())
                        .nickname(post.getMember().getNickname())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imageUrl(post.getImageUrl())
                        .count(post.getCount())
                        .category(post.getCategory())
                        .state(post.getState())
                        .modifiedAt(post.getModifiedAt())
                        .build()
                );
            }
        }
        return ResponseDto.success(postResponseDtoList);
    }

    // 카테고리 별로 게시글 조회하기
    @Transactional
    public ResponseDto<?> getPostsByCategory(Pageable pageable, Boolean isSaled, String category) {


        Page<Post> postList = postRepository.findAll(pageable);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        PostCategory categoryEnum = PostCategory.valueOf(category);


        for (Post post : postList) {
            if (categoryEnum.equals(post.getCategory())) {
                if (isSaled&&post.getState()) {
                    postResponseDtoList.add(PostResponseDto.builder()
                            .id(post.getPostId())
                            .nickname(post.getMember().getNickname())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .imageUrl(post.getImageUrl())
                            .count(post.getCount())
                            .category(post.getCategory())
                            .state(post.getState())
                            .modifiedAt(post.getModifiedAt())
                            .build()
                    );

                } else if(!isSaled) {
                    postResponseDtoList.add(PostResponseDto.builder()
                            .id(post.getPostId())
                            .nickname(post.getMember().getNickname())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .imageUrl(post.getImageUrl())
                            .count(post.getCount())
                            .category(post.getCategory())
                            .state(post.getState())
                            .modifiedAt(post.getModifiedAt())
                            .build()
                    );

                }
            }

        }
        return ResponseDto.success(postResponseDtoList);
    }



    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


}
