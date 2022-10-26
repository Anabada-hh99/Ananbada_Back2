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

import com.example.advanced.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {


  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;

  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional  //회원가입
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {

    //DB에 중복 아이디 있는지 확인
    if (memberRepository.existsByLoginName(requestDto.getLoginName())) {
      return ResponseDto.fail(CustomError.ALREADY_SAVED_LOGINNAME.name(),
                              CustomError.ALREADY_SAVED_LOGINNAME.getMessage());
    }

    //DB에 중복 닉네임 있는지 확인
    if (memberRepository.existsBynickname(requestDto.getNickname()))
    {
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
        //null
            MemberResponseDto.builder()
                    .memberId(member.getMemberId())
                    .loginName(member.getLoginName())
                    .nickname(member.getNickname())
                    .phoneNumber(member.getPhoneNumber())
                    .createdAt(member.getCreatedAt())
                    .modifiedAt(member.getModifiedAt())
                    .build()
    );
  }

  @Transactional  //Access-Token 만료되기 직전 Refresh-Token 발급
  public ResponseDto<?> reissue(HttpServletRequest request,
                                HttpServletResponse response) {

    System.out.println(request.getHeader("refresh_token"));
    System.out.println("------------------------------------------------------------------------------------------------");
    log.info("----------------------------------------------------------------log");
    //Validity of Refresh-Token not proven => INVALID_TOKEN
    if (!tokenProvider.validateToken(request.getHeader("refresh_token"))) {
      return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                              CustomError.INVALID_TOKEN.getMessage());
    }


    Member member = refreshTokenRepository.findByValue(request.getHeader("refresh_token"))
        .map(RefreshToken::getMember)
        .orElseThrow(() -> new IllegalArgumentException("RefreshToken not found"));

    RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(member);


    if (!refreshToken.getValue().equals(request.getHeader("refresh_token"))) {
      return ResponseDto.fail(CustomError.INVALID_TOKEN.name(), CustomError.INVALID_TOKEN.getMessage());
    }


    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    refreshToken.updateValue(tokenDto.getRefreshToken());
    tokenToHeaders(tokenDto, response);
    return ResponseDto.success(member);


    //FE side => Access-Token(x) Refresh-Token(o) => Access, Refresh 발급
    //Access-Token validate 하지 않으면 INVALID_TOKEN => Refresh
      //UsernamePasswordAuthenticationToken authenticationToken =
      //        new UsernamePasswordAuthenticationToken(member.getLoginName(), member.getPassword());
      //Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

      //여기까지 오면 이미 SecurityContextHolder에 Authentication이 있는데 Authentication 만들면 똑같은 정보 같은 애를 또 만드는 거 아닌가?
//      if (SecurityContextHolder.getContext().getAuthentication() == authentication){
//        //TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//        TokenDto tokenDto = tokenProvider.generateTokenDtoByMember(member);
//        refreshToken.updateValue(tokenDto.getRefreshToken());
//        tokenToHeaders(tokenDto, response);
//      }



//      if (!refreshToken.getValue().equals(request.getHeader("Refresh_token"))) {
//        return ResponseDto.fail(CustomError.INVALID_MEMBER.name(),
//                CustomError.INVALID_TOKEN.getMessage());
//      }

//      TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//      refreshToken.updateValue(tokenDto.getRefreshToken());
//      tokenToHeaders(tokenDto, response);

    //return ResponseDto.success("success");
//
//
//
//    if (!refreshToken.getValue().equals(request.getHeader("refresh_token"))) {
//      return ResponseDto.fail(CustomError.INVALID_MEMBER.name(),
//                              CustomError.INVALID_TOKEN.getMessage());
//    }
//
//    TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//    refreshToken.updateValue(tokenDto.getRefreshToken());
//    tokenToHeaders(tokenDto, response);
//    return ResponseDto.success("success");
  }


  @Transactional  //logout
  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("refresh_token"))) {
      return ResponseDto.fail(CustomError.INVALID_TOKEN.name(),
                              CustomError.INVALID_TOKEN.getMessage());
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
  public Member isPresentMember(String loginName) {
    Optional<Member> optionalMember = memberRepository.findByLoginName(loginName);
    return optionalMember.orElse(null);
  }



  @Transactional
  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("refresh_token", tokenDto.getRefreshToken());
    response.addHeader("Access_Token_Expire_Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

}
