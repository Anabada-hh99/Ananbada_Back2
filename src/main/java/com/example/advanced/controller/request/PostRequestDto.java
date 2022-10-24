package com.example.advanced.controller.request;

import com.example.advanced.domain.Member;
import com.example.advanced.domain.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
  private String title;
  private String content;
  private long price;
  private String imgUrl;
  private PostCategory category;
  private Boolean state;


}
