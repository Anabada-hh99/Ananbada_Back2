package com.example.advanced.controller;


import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;



    //게시글 작성
    @PostMapping(value = "/api/post")
    public ResponseDto<?> createPost(@RequestBody PostRequestDto postRequestDto,HttpServletRequest request) {
        return postService.createPost(postRequestDto,request);
    }

    //게시글 수정
    @PutMapping(value = "/api/post/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postRequestDto,HttpServletRequest request) {
        return postService.updatePost(postId,postRequestDto,request);
    }

    //게시글 삭제
    @DeleteMapping(value = "/api/post/{postId}")
    public ResponseDto<?> deletePost(@PathVariable Long postId,HttpServletRequest request) {
        return postService.deletePost(postId,request);
    }

    //게시글 상세 조회
    @GetMapping(value = "/api/post/{postId}")
    public ResponseDto<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    //물품판매상태 수정
    @PutMapping(path = "/api/post/{postId}/state")
    public ResponseDto<?> checkStock(@PathVariable Long postId,@RequestBody PostRequestDto postRequestDto,HttpServletRequest request) {
        return postService.checkStock(postId,postRequestDto,request);
    }



    //게시글 전체 조회
    @GetMapping(value = "/api/post")
    // @Pagable을 통해 보여줄 페이시 위치(0이 시작), 한 페이지에 게시글 개수(8), 정렬 기준(createdAt), 정렬 기준의 순서(내림차순)을 정의
    public ResponseDto<?> getAllPosts(@PageableDefault(page = 0, size = 8, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable,@RequestParam Boolean isSaled) {
        return postService.getAllPost(pageable,isSaled);
    }



    // 카테고리 별 게시글 조회
    @GetMapping(value = "/api/post/c")
    public ResponseDto<?> getPostsByCategory(@PageableDefault(page = 0, size = 8, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable,@RequestParam Boolean isSaled,@RequestParam String category) {
        return postService.getPostsByCategory(pageable,isSaled,category);
    }

    //조횟수 top4 게시글 조회
    @GetMapping(value = "/api/post/popular")
    public ResponseDto<?> getPostsByPopular() {
        return postService.getPostsByPopular();
    }







}
