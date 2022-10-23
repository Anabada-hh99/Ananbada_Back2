package com.example.advanced.controller.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

  @NotBlank(message = "아이디를 입력해주세요")
  private String loginName;

  @NotBlank(message = "비밀번호를 입력해주세요.")
  private String password;

}
