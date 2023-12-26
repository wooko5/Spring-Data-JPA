package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Test
    @DisplayName("스프링 데이터 JPA의 페이징 처리 테스트")
    public void paging(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        //첫 번째 페이지인 '0'에서 3 크기의 size를 'username' 기반으로 내림차순으로 가져와라
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username")); // 정렬도 복잡해지면 @Query에 넣는걸 추천

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //DTO로 변환함
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), member.getTeam().getName()));

        //then
        List<Member> content = page.getContent();
        long totalCount = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member == " + member);
        }
        System.out.println("totalCount == " + totalCount);

        assertThat(content.size()).isEqualTo(3); // 한 페이지의 크기
        assertThat(page.getTotalElements()).isEqualTo(5); // 전체 페이지의 데이터 크기
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호(index)
        assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 개수
        assertThat(page.isFirst()).isTrue(); // 현재 페이지가 첫 페이지 인가
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 존재하는가
    }

//    @Test
//    @DisplayName("스프링 데이터 JPA의 슬라이스 처리 테스트")
//    public void slicing(){
//        //given
//        memberRepository.save(new Member("member1", 10));
//        memberRepository.save(new Member("member2", 10));
//        memberRepository.save(new Member("member3", 10));
//        memberRepository.save(new Member("member4", 10));
//        memberRepository.save(new Member("member5", 10));
//
//        int age = 10;
//        //첫 번째 페이지인 '0'에서 3 크기의 size를 'username' 기반으로 내림차순으로 가져와라
//        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
//
//        //when
//        Slice<Member> slice = memberRepository.findByAge(age, pageRequest);
//
//        //then
//        List<Member> content = slice.getContent();
//
//        assertThat(content.size()).isEqualTo(3); // 한 페이지의 크기
//        assertThat(slice.getNumber()).isEqualTo(0); // 페이지 번호(index)
//        assertThat(slice.isFirst()).isTrue(); // 현재 페이지가 첫 페이지 인가
//        assertThat(slice.hasNext()).isTrue(); // 다음 페이지가 존재하는가
//
//        /**
//         * Slice는 전체 페이지의 데이터 크기, 전체 페이지 개수를 확인하는 메소드가 없음(totalCount 쿼리를 호출하지 않기 때문)
//         * slice.getTotalElements().isEqualTo(5); // X
//         * slice.getTotalPages().isEqualTo(2); // X
//         */
//    }
}