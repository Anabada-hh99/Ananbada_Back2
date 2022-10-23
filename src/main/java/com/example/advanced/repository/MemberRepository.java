package com.example.advanced.repository;


import com.example.advanced.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findById(Long id);
  Optional<Member> findByNickname(String nickname);

  Optional<Member> findByLoginName(String loginName);
  boolean existsByLoginName(String loginName);

  boolean existsBynickname(String loginName);
}
