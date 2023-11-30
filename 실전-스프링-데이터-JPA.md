## 실전-스프링-데이터-JPA



1. 프로젝트 환경설정

   - 프로젝트 생성

     - ```yaml
       groupId: study
       artifactId: data-jpa
       name: data-jpa
       Java 11
       빌드툴: Gradle
       라이브러리: web, jpa, h2, lombok
       SpringBootVersion: 2.7.17
       ```

     - build 설정 변경

       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/35a25867-8d93-4a92-9b34-d0d395499652)

     - annotation processors 설정 변경

       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/7a2cd760-a75e-49c7-950c-a1946288e6a6)

   - 라이브러리

     - 라이브러리간의 의존성 확인
       - `./gradlew dependencies --configuration compileClasspath`
       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/8f046027-d2b0-43e1-aba0-4a1da26d862c)
     - TIP
       - 스프링부트 2.0부터는 디폴트 Connection Pool(CP)가 Hikari CP임
       - 이전에는 Tomcat-JDBC를 사용, Hikari CP의 성능이 더 좋기 떄문에 바뀜
     - TODO: DB Connection Pool 개념과 Hikari CP의 성능이 더 좋은 이유?
       - 호기심 동기
         - 스프링부트 2.0부터는 디폴트 Connection Pool(CP)가 Hikari CP로 바뀌었는데 어떤 점 때문에 바뀌었을지 궁금해짐
       - DB Connection의 순서
         - 어플리케이션이 DB 드라이버를 통해 Connection을 맺음
         - 어플리케이션과 DB를 연결하기 위해 소켓이 열림(TCP, 3-way handshake)
         - 사용자 인증
         - DB 작업이 끝나면 Connection을 끊음
         - 소켓이 닫힘
       - DB Connection Pool?
         - DB 연결을 위해 미리 일정한 수의 Connection 객체를 미리 만들어서 Pool에 보관하고 매 요청마다 이미 생성된 Connection 객체를 넘겨주고, 작업이 끝나면 다시 Pool에 보관하는 방식을 DB Connection Pool 이라함
         - 이유
           - 클라이언트 요청시, DB와 어플리케이션을 연결/해제한다면 대규모 시스템에서는 비용이 굉장히 비싸고, 이는 성능(응답시간/처리량)에 영향을 끼칠 수 있음
           - 해결책: '미리 DB Connection을 많이 만들어놓고 쓰자' ==> DB 포화를 방지하고 일관된 성능을 기대할 수 있음
       - [가장 이상적인 Connection Pool 크기?](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing#the-formula)
         - `Connection size = (CPU 코어 개수*2 + DB 동시I/O처리개수) by Hikari 공식문서`
         - 600명의 사용자가 있다면, 15~20개 크기가 적당(좀더 조사해보자)
         - Connection Pool 크기를 무한정 늘린다고 해결되는 것이 아님
           - Connection 자체가 객체이기 때문에 생성을 많이 해두면 메모리를 추가로 사용하게 됨
           - Connection을 적게 생성하면 사용자가 요청을 기다리는 시간이 증가(Connection이 반납될 때까지 대기)
       - [Hikari CP](https://github.com/brettwooldridge/HikariCP#checkered_flag-jmh-benchmarks)의 성능이 더 좋은 이유
         - 적은 메모리 사용량
         - 높은 처리량
         - 작은 코드 베이스
         - 풍부한 옵션 구성
         - 쓰레드 안정성
         - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/71bc39eb-1054-448b-8558-d85964ee7f1a)

   - H2 DB 생성

     - [H2 설치경로](https://www.h2database.com/)
     - [스프링부트 프로젝트 가이드](https://spring.io/projects)
       - [스프링부트 해당 버전의 개요](https://spring.io/projects/spring-boot#learn)
       - [스프링부트 해당 버전의 적합한 라이브러리 의존성 버젼](https://docs.spring.io/spring-boot/docs/2.7.17/reference/html/dependency-versions.html#appendix.dependency-versions)
     -  최초 실행 시
       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/79adfe5a-5cc7-4eb5-834e-13d733cb2743)
       - ~/datajpa.mv.db 파일 생성 확인
       - 위처럼 접속하면 파일에 lock이 걸려서 다른 곳에서 접근이 불가하니 아래처럼 수정하자
     - 이후 실행 시
       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/f4bd56fa-c7ac-4981-b297-0c67e85a0858)

   - 스프링 데이터 JPA와 DB 설정, 동작 확인

     - application.yml 추가

       - ```yaml
         spring:
           datasource:
             url: jdbc:h2:tcp://localhost/~/datajpa
             username: sa
             password:
             driver-class-name: org.h2.Driver
         
           jpa:
             hibernate:
               ddl-auto: create
             properties:
               hibernate:
         #        show_sql: true # 콘솔창에 하이버네이트 실행 SQL을 남김
                 format_sql: true
         
         logging.level:
           org.hibernate.SQL: debug # logger를 통해 하이버네이트 실행 SQL을 남김
         #  org.hibernate.type: trace # SQL에 던지는 파라미터를 출력해주는 옵션
         ```

     - test class 만들기 (`Ctrl + Shift + T`)
     
       - ```java
         /**
          * JUnit5 테스트 시, `@SpringBootTest`만 써줘도 기존의 `@RunWith(SpringRunner.class)`를 대체함
          */
         @SpringBootTest
         class MemberJpaRepositoryTest {
         
             @Test
             void save() {
             }
         
             @Test
             void find() {
             }
         }
         ```

     - 엔티티의 기본 생성자를 protected로 놓는 이유

       - JPA 구현체인 hibernate에서 엔티티를 proxy 객체로 강제로 생성할 때, 접근제어자가 private으로 되어있으면 막혀서 생성 못 함. 그러므로 protected로 놓으라고 JPA 명세에 나옴
     
       - ```java
         @Entity
         @Getter
         @NoArgsConstructor(access = AccessLevel.PROTECTED)
         public class Member {
         
             @Id
             @GeneratedValue
             private Long id;
             private String username;
         
             public Member(String username) {
                 this.username = username;
             }
         }
         ```

     - 콘솔창에 파라미터 출력 - 해당 라이브러리 작성
     
       - ```groovy
         implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.7'
         ```
     
       - 개발 서버에서는 몰라도, 운영 서버에서는 많은 로그를 남기는 것이 성능에 영향을 끼칠 수 있기 때문에 신중하게 선택해야함
       
     - TODO: Optional을 언제 사용하는게 적절할까?
     
       - 호기심 동기
     
         - `JpaRepository<T, id>`를 구현한 인터페이스로 대부분의 쿼리를 JPA가 대신 생성하는데 `findById`의 경우, Optional 체크를 강요함. Optional의 올바른 사용법이 궁금해짐
     
       -  올바른 경우, [출처](https://mangkyu.tistory.com/203)
     
         - ```
           1) Optional 변수에 null을 할당하지 않기
           2) 값이 없을 때, Optional.orElseXxx()로 기본값을 반환하자
           3) 단순히 값을 얻을려고 하면 Optional을 사용하지 말자
           4) 생성자, 수정자, 메소드의 파라미터로 사용하지 말자
           5) Collection의 경우 Optional이 아닌 비어있는 Collection을 사용하라
           6) return 타입으로만 사용하라
           ```
     
       - Optional 변수에 null을 할당하지 않기
       
         - ```java
           Optional<Member> optionalMember = Optional.empty(); // prefer
           Optional<Member> optionalMember = null; // avoid
           ```
       
       - 값이 없을 때, Optional.orElseXxx()로 기본값을 반환하자
       
         - ```java
           Member foundMember = optionalMember.orElse(new Member("emptyMember")); // prefer
           Member foundMember = optionalMember.isPresent() ? optionalMember.get() : null; // avoid
           ```
       
       - 단순히 값을 얻을려고 하면 Optional을 사용하지 말자
       
         - ```java
           Member foundMember = optionalMember == null ? new Member("emptyMember") : optionalMember; // prefer
           Member foundMember = Optional.ofNullable(optionalMember).orElse(new Member("emptyMember")); // avoid
           ```
       
       - 생성자, 수정자, 메소드의 파라미터로 사용하지 말자
       
         - ```java
           // avoid
           public class Member {
               
               private final Optional<Long> id;
               private final String username;
           
               public Customer(String username, Optional<Long> id) {
                   this.username = Objects.requireNonNull(username, () -> "Cannot be null");
                   this.id = id;
               }
           
               public Optional<String> getUsername() {
                   return Optional.ofNullable(username);
               }
               
               public Optional<String> getId() {
                   return id;
               }
           }
           ```
       
       - Collection의 경우 Optional이 아닌 비어있는 Collection을 사용하라
       
         - ```java
           private List<Member> memberList = ...; // null이 올 수 있음
           
           // avoid
           public Optional<List<Member>> getMemberList() {    
               return Optional.ofNullable(memberList);
           }
           
           // prefer
           public List<Member> getMemberList() {
               return memberList == null ? Collections.emptyList() : memberList;
           }
           ```
       
       - return 타입으로만 사용하라(수정중)
       
         - ```java
           private Member member = new Member("Jaeuk");
           private Member savedMember = memberRepository.save(member);
           
           // avoid
           public Boolean getUsername(){
               Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
               return optionalMember.isPresent() ? true : false;
           }
           
           // prefer
           public Optional<Member> getUsername(){
               return memberRepository.findById(savedMember.getId());
           }
           ```
       
       - 결론
       
         - Optional을 잘못 사용하는 것은 차라리 쓰지 않는 것만 못 하므로 써야할 때와 쓰지 말아야할 때를 구분하자

2. 예제 도메인 모델

   - 예제 도메인 모델과 동작확인
     - Entity class
     
       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/d232c65d-9b24-4702-a5d2-f5b238a68dbd)
     
     - ERD
     
       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/2f3843da-e895-4a4f-9898-a267ba47d40a)
     
     - MappedBy는 FK를 가지고있지 않은 Entity에 선언함
     
       - ```java
         @Entity
         @Getter
         @NoArgsConstructor(access = AccessLevel.PROTECTED)
         @ToString(of = {"id", "name"})
         public class Team {
         
             @Id @GeneratedValue
             @Column(name = "team_id")
             private Long id;
             private String name;
             @OneToMany(mappedBy = "team")
             private List<Member> members = new ArrayList<>();
         
             public Team(String name) {
                 this.name = name;
             }
         }
         
         @Entity
         @Getter
         @NoArgsConstructor(access = AccessLevel.PROTECTED)
         @ToString(of = {"id", "username", "age"})
         // team을 해당 어노테이션에 넣으면 양방향 관계이기 때문에 무한참조가 발생할 수 있음, 가급적이면 연관관계는 @ToString에 넣지말자
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
         ```
     
         

3. 공통 인터페이스 기능

   - 순수 JPA 기반 리포지토리(Repository) 만들기

     - CRUD로 기본적인 로직 생성

       - MemberJpaRepository

         - ```java
           @Repository
           public class MemberJpaRepository {
           
               @PersistenceContext
               private EntityManager entityManager;
           
               public Member save(Member member) {
                   entityManager.persist(member);
                   return member;
               }
           
               //CRUD중 U(수정)가 없는 이유는 JPA는 변경감지를 통해 영속성 컨텍스트가 관리하는 영속 상태의 엔티티를 수정함
           
               public void delete(Member member) {
                   entityManager.remove(member);
               }
           
               public List<Member> findAll() {
                   return entityManager.createQuery("select m from Member m", Member.class).getResultList();
               }
           
               public Optional<Member> findById(Long memberId){
                   return Optional.ofNullable(entityManager.find(Member.class, memberId));
               }
           
               public long count(){
                   return entityManager.createQuery("select count(m) from Member m", Long.class).getSingleResult();
               }
           }
           ```

     - TODO: Dirty Checking의 순서

       - ```
         1) @Transactional처럼 트랜잭션을 커밋하겠다고 비즈니스 로직에서 JPA에게 요청을 보내면 flush()가 호출
         
         2) 해당 시점에 영속성 컨텍스트가 엔티티와 스냅샷을 비교해서 차이가 존재한다면 UPDATE문을 생성해서 '쓰기 지연' SQL 저장소에 저장
         
         3) 쓰기 지연 SQL 저장소에 있는 쿼리문을 DB에 전송
         
         4) DB에서 트랜잭션을 커밋 
         ```

   - 공통 인터페이스 설정

     - javaConfig 설정

       - ```java
         package study.datajpa;
         
         import org.springframework.boot.SpringApplication;
         import org.springframework.boot.autoconfigure.SpringBootApplication;
         
         @SpringBootApplication
         //@EnableJpaRepositories(basePackages = "study.datajpa.repository") // 스프링부트를 쓰면 굳이 쓸 필요가 없음
         public class DataJpaApplication {
         
             public static void main(String[] args) {
                 SpringApplication.run(DataJpaApplication.class, args);
             }
         
         }
         
         ```

     - 스프링 데이터 JPA가 JpaRepository를 상속한 인터페이스의 구현체를 생성

       - ![image](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/4b97dda4-c9c9-4764-bf7e-66034a98e6e7)

       - ```java
         public interface MemberRepository extends JpaRepository<Member, Long> {
             // 구현체가 없는 interface인데 JPA가 알아서 구현 클래스를 생성함
         }
         ```

         

   - 공통 인터페이스 적용

   - 공통 인터페이스 분석

4. 쿼리 메소드 기능

5. 확장 기능

6. 스프링 데이터 JPA 분석

7. 나머지 기능들
