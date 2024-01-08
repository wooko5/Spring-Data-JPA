package study.datajpa.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Transactional
class MemberTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team arsenal = new Team("Arsenal");
        Team tottenham = new Team("Tottenham");
        entityManager.persist(arsenal);
        entityManager.persist(tottenham);

        Member henry = new Member("henry", 30, arsenal);
        Member harry = new Member("harry", 20, tottenham);
        Member odegaard = new Member("odegaard", 25, arsenal);
        Member son = new Member("son", 29, tottenham);

        entityManager.persist(henry);
        entityManager.persist(harry);
        entityManager.persist(odegaard);
        entityManager.persist(son);

        //초기화
        entityManager.flush();
        entityManager.clear();

        //확인
        List<Member> members = entityManager.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("=============START===============");
            System.out.println("member = " + member);
            System.out.println("team = " + member.getTeam());
            System.out.println("=============END===============");
        }
    }

    @Test
    @DisplayName("순수 JPA로 등록/수정일시 칼럼 생성 테스트")
    public void jpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member("memberA");
        memberRepository.save(member); //@PrePersist

        Thread.sleep(100);
        member.setUsername("memberB");

        entityManager.flush(); //@PreUpdate
        entityManager.clear();

        //when
        Member foundMember = memberRepository.findMemberByUsername("memberB");

        //then
        System.out.println("=================================================================");
        System.out.println("foundMember.getCreatedDate() == " + foundMember.getCreatedDate());
        System.out.println("foundMember.getUpdatedDate() == " + foundMember.getUpdatedDate());
        System.out.println("=================================================================");
        assertThat(foundMember.getId()).isEqualTo(member.getId());
    }
}