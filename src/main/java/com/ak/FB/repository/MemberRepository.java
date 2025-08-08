package com.ak.FB.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ak.FB.model.Member;
import java.util.List;
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
       Member findByEmail(String email); // This should return a single Member
   
}