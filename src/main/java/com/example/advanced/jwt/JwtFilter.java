package com.example.advanced.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Access_Token";
  public static final String BEARER_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;

  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    String jwt = resolveToken(request);
  //  String refreshjwt = resolveRefreshToken(request);

    //Michin 이거 때문인가?
    if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
      Authentication authentication = tokenProvider.getAuthentication(jwt);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

//    if (StringUtils.hasText(refreshjwt) && tokenProvider.validateToken(refreshjwt) && (!StringUtils.hasText(jwt))){
//      //get Authentication에서 왜 계속 권한 정보 없다고 떠? 아오
//      Authentication refreshAuthentication = tokenProvider.getAuthentication(refreshjwt);
//      SecurityContextHolder.getContext().get
//    }

    filterChain.doFilter(request, response);
  }

//  private String resolveRefreshToken(HttpServletRequest request) {
//    String refreshToken = request.getHeader("refresh_token");
//    if (StringUtils.hasText(refreshToken)){
//      System.out.println();
//      return refreshToken;
//    }
//    return null;
//  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
