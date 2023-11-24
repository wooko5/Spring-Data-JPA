package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() {
        Member member = new Member("Jaeuk");
        Member savedMember = memberRepository.save(member);
        Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
        //TODO: 다른 방식으로 고칠 수는 없을까?, orElse/orElseGet/orElseThrow 중 선택, 단순하게 값을 넘기는건 지양해야하나
        Member foundMember = optionalMember.orElse(new Member("emptyMember"));
        assertThat(foundMember.getId()).isEqualTo(member.getId());
        assertThat(foundMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(foundMember).isEqualTo(member);
    }
}