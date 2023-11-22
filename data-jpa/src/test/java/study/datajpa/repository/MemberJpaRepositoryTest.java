package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

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
}