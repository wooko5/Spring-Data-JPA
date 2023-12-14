package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy(); // HelloBy만 쓰면 오류지만 findTop3 때문에 유효한 리미트 Query가 출력

    @Query(name = "Member.findByUsername") // Member 엔티티에서 해당 NameQuery를 찾음
    List<Member> findByUsername(@Param("username") String username); // 실무에서는 거의 쓸 일이 없음
}
