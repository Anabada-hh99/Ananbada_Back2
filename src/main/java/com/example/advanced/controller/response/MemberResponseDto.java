package com.example.advanced.controller.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
  private Long memberId;
  private String loginName;
  private String nickname;
  private String phoneNumber;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
