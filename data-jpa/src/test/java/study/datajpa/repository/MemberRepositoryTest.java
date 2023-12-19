package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

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

    @Test
    @DisplayName("메소드 이름 쿼리 조회 테스트1")
    void findByUsernameAndGreaterThenAge() {
        Member a = new Member("AAA", 10);
        Member b = new Member("AAA", 20);
        memberRepository.save(a);
        memberRepository.save(b);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }


    @Test
    @DisplayName("메소드 이름 쿼리 조회 테스트2")
    void findTop3HelloBy() {
        List<Member> result = memberRepository.findTop3HelloBy();
    }

    @Test
    @DisplayName("NamedQuery 테스트")
    public void testNameQuery() {
        Member a = new Member("AAA", 10);
        Member b = new Member("BBB", 20);
        memberRepository.save(a);
        memberRepository.save(b);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember.getAge()).isEqualTo(a.getAge());
    }

    @Test
    @DisplayName("쿼리로 직접 조회 테스트")
    public void testQuery() {
        Member a = new Member("AAA", 10);
        Member b = new Member("BBB", 20);
        memberRepository.save(a);
        memberRepository.save(b);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(a);
    }

    @Test
    public void findUsernameList() {
        Member a = new Member("AAA", 10);
        Member b = new Member("BBB", 20);
        memberRepository.save(a);
        memberRepository.save(b);

        List<String> result = memberRepository.findUsernameList();
        for (String username : result) {
            System.out.println("username == " + username);
        }
    }

    @Test
    @DisplayName("DTO 조회 테스트")
    public void findMemberDto() {
        Team team = new Team("Arsenal");
        teamRepository.save(team);

        Member a = new Member("AAA", 10);
        a.setTeam(team);
        memberRepository.save(a);


        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result) {
            System.out.println("dto == " + dto);
        }
    }

    @Test
    @DisplayName("파라미터 바인딩 테스트 - 위치기반, 컬렉션 파라미터")
    public void findByNames() {
        Member a = new Member("AAA", 10);
        Member b = new Member("BBB", 20);
        memberRepository.save(a);
        memberRepository.save(b);


        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member == " + member);
        }
    }

    @Test
    @DisplayName("반환타입 테스트")
    public void returnType() {
        Member a = new Member("AAA", 10);
        Member b = new Member("BBB", 20);
        memberRepository.save(a);
        memberRepository.save(b);


        List<Member> aaa = memberRepository.findListByUsername("B"); //JPA에서 List는 null 체크 코드를 따로 만들지 않아도 된다. 왜냐하면 JPA가 알아서 empty 체크를 해서 빈 컬렉션을 생성하기 때문
        Member bbb = memberRepository.findMemberByUsername("C");
        Optional<Member> ccc = memberRepository.findOptionalByUsername("D");

        System.out.println("aaa == " + aaa);
        System.out.println("bbb == " + bbb);
        System.out.println("ccc == " + ccc.orElse(new Member("John Doe")));
    }
}