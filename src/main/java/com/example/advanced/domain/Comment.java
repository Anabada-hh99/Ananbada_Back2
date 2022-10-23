package com.example.advanced.domain;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="member_Id", nullable = false)
    private Member member;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="post_Id", nullable = false)
    private Post post;


}
