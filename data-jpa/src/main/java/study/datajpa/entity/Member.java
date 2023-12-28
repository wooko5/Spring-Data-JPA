package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter //TODO: Setter 지우기
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
// team을 해당 어노테이션에 넣으면 양방향 관계이기 때문에 무한참조가 발생할 수 있음, 가급적이면 연관관계는 @ToString에 넣지말자
@NamedQuery(
        name="Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
//@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) //자주 쓰이지 않음 참고용
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //XxxToOne은 지연로딩으로 만들자
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this); //team과 member는 양방향 연관관계이기 때문에 team을 바꾸면 team의 member 변수에도 수정해야함
    }
}
