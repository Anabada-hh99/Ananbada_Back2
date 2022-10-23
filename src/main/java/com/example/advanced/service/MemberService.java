package com.example.advanced.service;

import com.example.advanced.controller.handler.CustomError;
import com.example.advanced.controller.request.LoginRequestDto;
import com.example.advanced.controller.request.MemberRequestDto;
import com.example.advanced.controller.request.TokenDto;
import com.example.advanced.controller.response.MemberResponseDto;
import com.example.advanced.controller.response.ResponseDto;
import com.example.advanced.domain.Member;
import com.example.advanced.domain.RefreshToken;
import com.example.advanced.domain.UserDetailsImpl;
import com.example.advanced.jwt.TokenProvider;
import com.example.advanced.repository.MemberRepository;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {


  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;

  @Transactional  //회원가입
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {

    //DB에 중복 아이디 있는지 확인
    if (memberRepository.existsByLoginName(requestDto.getLoginName())){
      return ResponseDto.fail(CustomError.ALREADY_SAVED_LOGINNAME.name(),
                              CustomError.ALREADY_SAVED_LOGINNAME.getMessage());
    }

    //DB에 중복 닉네임 있는지 확인
    if (memberRepository.existsBynickname(requestDto.getNickname())){
      return ResponseDto.fail(CustomError.ALERADY_SAVED_NICKNAME.name(),
                              CustomError.ALERADY_SAVED_NICKNAME.getMessage());
    }

    Member member = Member.builder()
            .loginName(requestDto.getLoginName())
            .nickname(requestDto.getNickname())
            .password(passwordEncoder.encode(requestDto.getPassword()))
            .phoneNumber(requestDto.getPhoneNumber())
            .build();
    memberRepository.save(member);

    return ResponseDto.success(
                  null
        );
  }

  @Transactional  //로그인
  public ResponseDto<?> signIn(LoginRequestDto requestDto, HttpServletResponse response) {

    //DB loginName Check
    Member member = isPresentMember(requestDto.getLoginName());
    if (null == member) {
      return ResponseDto.fail(CustomError.MEMBER_NOT_FOUND.name(),
                              CustomError.MEMBER_NOT_FOUND.getMessage());
    }

    //DB password check
    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail(CustomError.INVALID_MEMBER.name(),
                              CustomError.INVALID_MEMBER.getMessage());
    }

    //로그인 할 때 Authentication(인증 객체 생성)
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(requestDto.getLoginName(), requestDto.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    //Access-Token, Refresh-Token 발급한 후 FE에 ServletResponse로 전달
    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        null
    );
  }

  @Transactional  //Access-Token 만료되기 직전 Refresh-Token 발급
  public ResponseDto<?> reissue(HttpServletRequest request,
                                HttpServletResponse response) {

    if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
      return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                              CustomError.INVALID_MEMBER.getMessage());
    }

    Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Access_Token"));
    Member member = ((UserDetailsImpl) authentication.getPrincipal()).getMember();
    RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(member);


    if (!refreshToken.getValue().equals(request.getHeader("Refresh_Token"))) {
      return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                              CustomError.INVALID_TOKEN.getMessage());
    }

    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
    refreshToken.updateValue(tokenDto.getRefreshToken());
    tokenToHeaders(tokenDto, response);
    return ResponseDto.success("success");
  }


  @Transactional  //logout
  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh_Token"))) {
      return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                              CustomError.INVALID_MEMBER.getMessage());
    }

    //Authentication에 있는 member 찾기
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail(CustomError.MEMBER_NOT_FOUND.name(),
                              CustomError.MEMBER_NOT_FOUND.getMessage());
    }

    //Refresh-Token까지 삭제하기
    return tokenProvider.deleteRefreshToken(member);

  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
    return optionalMember.orElse(null);
  }

  @Transactional
  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Access_Token", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh_Token", tokenDto.getRefreshToken());
    response.addHeader("Access_Token_Expire_Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}
