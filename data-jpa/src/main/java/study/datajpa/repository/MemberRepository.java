package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); // (1)메소드이름으로 쿼리생성

    List<Member> findTop3HelloBy(); // HelloBy만 쓰면 오류지만 findTop3 때문에 유효한 리미트 Query가 출력

//    @Query(name = "Member.findByUsername") // (2)NameQuery: Member 엔티티에서 해당 쿼리를 찾음
    List<Member> findByUsername(@Param("username") String username); // 실무에서는 거의 쓸 일이 없음

    @Query("select m from Member m where m.username = :username and m.age = :age") // (3)@Query로 쿼리문 직접정의
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
}
