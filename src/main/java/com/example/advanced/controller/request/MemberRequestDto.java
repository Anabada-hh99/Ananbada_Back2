package com.example.advanced.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

  //로그인 후 홈페이지에서 사용할 닉네임
  @NotBlank(message = "닉네임 입력은 필수입니다.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{4,12}$",
          message = "닉네임은 숫자와 영어를 포함한 4-12글자여야합니다.")
  private String nickname;


  //로그인 시 필요한 아이디
  @NotBlank(message = "아이디 입력은 필수입니다.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{4,12}$",
          message = "아이디는 숫자와 영어를 포함한 4-12글자여야합니다.")
  private String loginName;

  //비밀번호
  @NotBlank(message = "비밀번호 입력은 필수입니다.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$",
          message = "비밀번호는 영문 대소문자와 숫자,특수문자를 포함한 8-20자여야합니다.")
  private String password;


  @NotBlank(message = "전화번호 입력은 필수입니다.")
  @Size(min = 8, max = 12, message = "8-12자리의 전화번호를 입력해주세요")
  private String phoneNumber;
}
