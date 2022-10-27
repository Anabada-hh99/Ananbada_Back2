package com.example.advanced.controller;


import com.example.advanced.controller.request.PostRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    //게시글 작성
    @PostMapping(value = "/post/create")
    public ResponseDto<?> createPost(@RequestPart PostRequestDto postRequestDto,
                                     HttpServletRequest request,
                                     @RequestParam(name="file",required = false)MultipartFile multipartFile) throws IOException {
        return postService.createPost(postRequestDto,request,multipartFile);
    }

    //게시글 수정
    @PutMapping(value = "/post/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId,
                                     @RequestBody PostRequestDto postRequestDto,
                                     HttpServletRequest request) {
        return postService.updatePost(postId,postRequestDto,request);
    }

    //게시글 삭제
    @DeleteMapping(value = "/post/{postId}")
    public ResponseDto<?> deletePost(@PathVariable Long postId,HttpServletRequest request) {
        return postService.deletePost(postId,request);
    }

    //게시글 상세 조회
    @GetMapping(value = "/post/{postId}")
    public ResponseDto<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }


    //물품판매상태 수정
    @PutMapping(path = "/post/{postId}/state")
    public ResponseDto<?> checkStock(@PathVariable Long postId,@RequestBody PostRequestDto postRequestDto,HttpServletRequest request) {
        return postService.checkStock(postId,postRequestDto,request);
    }

    //게시글 전체 조회
    @GetMapping(value = "/post")
    // @Pagable을 통해 보여줄 페이시 위치(0이 시작), 한 페이지에 게시글 개수(8), 정렬 기준(createdAt), 정렬 기준의 순서(내림차순)을 정의
    public ResponseDto<?> getAllPosts(@PageableDefault(page = 0, size = 9, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable,
                                      @RequestParam Boolean isSaled) {
        return postService.getAllPost(pageable,isSaled);
    }

    // 카테고리 별 게시글 조회
    @GetMapping(value = "//post/c")
    public ResponseDto<?> getPostsByCategory(@PageableDefault(page = 0, size = 9, sort = "modifiedAt", direction = Sort.Direction.DESC) Pageable pageable,
                                             @RequestParam Boolean isSaled,@RequestParam String category) {
        return postService.getPostsByCategory(pageable,isSaled,category);
    }


    //조회수TOP4 게시글 조회
    @GetMapping(value = "/post/p")
    public ResponseDto<?> getPostsByCount(@PageableDefault(size = 4, sort = "count", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return postService.getPostsByCount(pageable);
    }









}
