package com.example.advanced.controller;

import com.example.advanced.controller.request.LoginRequestDto;
import com.example.advanced.controller.request.MemberRequestDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  //회원가입
  @PostMapping(value = "/members/signup")
  public ResponseDto<?> signup(@Valid @RequestBody MemberRequestDto requestDto){
    return memberService.createMember(requestDto);
  }

  //로그인
  @PostMapping(value = "/members/signin")
  public ResponseDto<?> signin(@RequestBody LoginRequestDto requestDto,
                               HttpServletResponse response){
    return memberService.signIn(requestDto, response);
  }

  //로그아웃
  @PostMapping(value = "/members/logout")
  public ResponseDto<?> logout(HttpServletRequest request){
    return memberService.logout(request);
  }

  @ApiImplicitParams({
          @ApiImplicitParam(
                  name = "Refresh_Token",
                  required = true,
                  dataType = "string",
                  paramType = "header"
          )
  })

  @PostMapping(value = "/api/members/reissue")
  public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
    return memberService.reissue(request, response);
  }

}
