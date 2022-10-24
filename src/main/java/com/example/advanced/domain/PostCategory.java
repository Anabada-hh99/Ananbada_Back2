package com.example.advanced.domain;

import lombok.Getter;

import java.util.Arrays;
@Getter
public enum PostCategory {
    //스포츠, 의류, 식품, 가전제품, 뷰티, 기타
    SPORT("스포츠"),
    CLOTH("의류"),
    FOOD("식품"),
    HOME("가전제품"),
    BEAUTY("뷰티"),
    ETC("기타");

    private String value;


    PostCategory(String value) {
        this.value = value;
    };

    public static PostCategory fromCode(String dbData){
        return Arrays.stream(PostCategory.values())
                .filter(v -> v.getValue().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당카테고리가 존재하지않습니다",dbData)));
    };


}
