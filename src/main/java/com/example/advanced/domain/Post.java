package com.example.advanced.domain;

import com.example.advanced.controller.request.PostRequestDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends Timestamped{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String imageUrl;

    //조회 수
    @Column(nullable = false)
    private Integer count;


    @Convert(converter = PostCategoryConverter.class)
    private PostCategory category;

    //판매상태
    @Column(nullable = false)
    private Boolean state;


    @JsonManagedReference
    @OneToMany(mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="member_Id", nullable = false)
    private Member member;

    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.price = postRequestDto.getPrice();
        this.imageUrl = postRequestDto.getImgUrl();
        this.category = postRequestDto.getCategory();
    }

    public void updateState(boolean state) {
        this.state = state;
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }

}
