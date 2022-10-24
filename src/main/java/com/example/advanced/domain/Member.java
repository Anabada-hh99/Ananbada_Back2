package com.example.advanced.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long memberId;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  private String loginName;

  @Column(nullable = false)
  @JsonIgnore
  private String password;

  @Column(nullable = false)
  private String phoneNumber;

  @JsonManagedReference
  @OneToMany(fetch = FetchType.LAZY,
          mappedBy = "member",
          cascade = CascadeType.ALL,
          orphanRemoval = true
  )
  private List<Post> posts = new ArrayList<>();

  @JsonManagedReference
  @OneToMany(fetch = FetchType.LAZY,
          mappedBy = "member",
          cascade = CascadeType.ALL,
          orphanRemoval = true
  )
  private List<Comment> comments = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Member member = (Member) o;
    return memberId != null && Objects.equals(memberId, member.memberId);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
    return passwordEncoder.matches(password, this.password);
  }
}
