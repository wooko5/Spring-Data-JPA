package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("Jaeuk");
        System.out.println("memberRepository == " + memberRepository.getClass());
        Member savedMember = memberRepository.save(member);
        Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
        //TODO: 다른 방식으로 고칠 수는 없을까?, orElse/orElseGet/orElseThrow 중 선택, 단순하게 값을 넘기는건 지양해야하나
        Member foundMember = optionalMember.orElse(new Member("emptyMember"));
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(foundMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member memberA = new Member("Apple");
        Member memberB = new Member("Microsoft");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //단건 조회 검증
        Member findMemberA = memberRepository.findById(memberA.getId()).orElseThrow();
        Member findMemberB = memberRepository.findById(memberB.getId()).orElseThrow();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        //다건 조회 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);


        //삭제 검증
        memberRepository.delete(memberA);
        memberRepository.delete(memberB);
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }
}