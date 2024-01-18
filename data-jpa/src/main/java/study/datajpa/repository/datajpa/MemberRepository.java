package study.datajpa.repository.datajpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberProjection;
import study.datajpa.repository.projection.UsernameOnly;
import study.datajpa.repository.projection.UsernameOnlyDto;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age); //(1)메소드이름으로 쿼리생성

    List<Member> findTop3HelloBy(); //HelloBy만 쓰면 오류지만 findTop3 때문에 유효한 리미트 Query가 출력

//    @Query(name = "Member.findByUsername") //(2)NamedQuery: Member 엔티티에서 해당 쿼리를 찾음
    List<Member> findByUsername(@Param("username") String username); //실무에서는 거의 쓸 일이 없음

    @Query("select m from Member m where m.username = :username and m.age = :age") //(3)@Query로 쿼리문 직접정의
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names); //(4)파라미터 바인딩(컬렉션 파라미터 바인딩, 이름기반), 다양한 input을 위해 List에서 Collection으로 바꿈

    List<Member> findListByUsername(String username); //컬렉션

    Member findMemberByUsername(String username); //단건

    Optional<Member> findOptionalByUsername(String name); //단건 Optional

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable); //스프링 데이터 JPA 페이징과 정렬
//    Slice<Member> findByAge(int age, Pageable pageable); //스프링 데이터 JPA 페이징과 정렬

    @Modifying(clearAutomatically = true) //해당 어노테이션이 있어야 .executeUpdate() 같은 역할을 함
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"}) // Member 조회 시, 연관된 엔티티 중에 Fetch Join으로 한번에 가져올 엔티티를 선언하는 어노테이션
    List<Member> findAll();

    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraph(); // findAll() 쿼리와 같은 것

    @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username); //projection

    List<UsernameOnlyDto> findClassProjectionsByUsername(@Param("username") String username); //클래스 기반 projection

    <T> List<T> findClassProjectionsByUsername(@Param("username") String username, Class<T> type); //제네릭을 이용한 동적 projection

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName from member m left join team t"
            ,countQuery = "select count(*) from member"
            ,nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable); //네이티브 쿼리를 굳이 써야한다면 DTO를 조회하는 projection 방식을 쓰자
}
