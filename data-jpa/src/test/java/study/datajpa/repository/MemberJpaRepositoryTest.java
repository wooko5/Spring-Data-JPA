package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JUnit5 테스트 시, `@SpringBootTest`만 써줘도 기존의 `@RunWith(SpringRunner.class)`를 대체함
 */
@SpringBootTest
@Transactional
//@Rollback(value = false) // 테스트 데이터를 보고싶다면 해당 어노테이션을 작성
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member foundMember = memberJpaRepository.find(savedMember.getId());
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(foundMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member memberA = new Member("Apple");
        Member memberB = new Member("Microsoft");
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        //단건 조회 검증
        Member findMemberA = memberJpaRepository.findById(memberA.getId()).orElseThrow();
        Member findMemberB = memberJpaRepository.findById(memberB.getId()).orElseThrow();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        //다건 조회 검증
        List<Member> members = memberJpaRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(memberA);
        memberJpaRepository.delete(memberB);
        assertThat(memberJpaRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void findByUsernameAndGreaterThenAge() {
        Member a = new Member("AAA", 10);
        Member b = new Member("AAA", 20);
        memberJpaRepository.save(a);
        memberJpaRepository.save(b);

        List<Member> result = memberJpaRepository.findByUsernameAndGreaterThanAge("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void findByUsername() {
        Member a = new Member("AAA", 10);
        Member b = new Member("BBB", 20);
        memberJpaRepository.save(a);
        memberJpaRepository.save(b);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(a);
    }
}