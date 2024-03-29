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
     - **TODO: DB Connection Pool 개념과 Hikari CP의 성능이 더 좋은 이유?**
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
       
     - **TODO: Optional을 언제 사용하는게 적절할까?**
     
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
                   return entityManager.createQuery("select m from Member m", Member.class)
                       .getResultList();
               }
           
               public Optional<Member> findById(Long memberId){
                   return Optional.ofNullable(entityManager.find(Member.class, memberId));
               }
           
               public long count(){
                   return entityManager.createQuery("select count(m) from Member m", Long.class)
                       .getSingleResult();
               }
           }
           ```

     - **TODO: Dirty Checking의 순서**

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

     - 주의

       - ```
         변경사항
         - T findOne(ID) ==> Optional<T> findById(ID)로 변경
         - boolean exists(ID) ==> boolean existsById(ID)로 변경
         
         제네릭 타입
         - T  : 엔티티
         - ID : 엔티티의 식별자 타입
         - S  : 엔티티와 그 자식 타입
         ```

4. 쿼리 메소드 기능

   - 메소드이름으로 쿼리생성
     - JPA에서 제공하는 기능으로 쿼리 생성하기
       - https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
     
     - 비교
     
       - 순수 JPA repository
     
         - ```java
           public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
               return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                   .setParameter("username", username)
                   .setParameter("age", age)
                   .getResultList();
           }
           ```
     
       - 스프링 DATA JPA
     
         - ```java
           public interface MemberRepository extends JpaRepository<Member, Long> { 
               List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
           }
           ```
     
     - 참고
     
       - 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 한다. 그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생
       - 이렇게 애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점임
     
   - JPA NamedQuery

     - 실무에서 잘 안 쓰는 이유

       - 다음 수업 시간에 배울 `@Query 어노테이션을 통해 레포지토리에 직접 쿼리를 입력하는 방법`이 있어서 실무에서 사용할 가능성이 낮음

     - 우선순위

       - NamedQuery를 찾고, 해당 메소드가 없다면 메소드이름으로 쿼리를 생성하는 걸 찾음

     - 코드

       - ```java
         public interface MemberRepository extends JpaRepository<Member, Long> {
             @Query(name = "Member.findByUsername") // Member 엔티티에서 해당 쿼리를 찾음
             List<Member> findByUsername(@Param("username") String username); // 실무에서는 거의 쓸 일이 없음
         }
         ```

     - 장점

       - 애플리케이션 로딩 시, 정적 쿼리이기 때문에 컴파일 시에 문법 오류가 있는지 확인할 수 있음 

   - `@Query` 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의

     - 코드

       - ```java
         public interface MemberRepository extends JpaRepository<Member, Long> {
             @Query("select m from Member m where m.username = :username and m.age = :age")
             List<Member> findUser(@Param("username") String username, @Param("age") int age);
         }
         ```

     - 장점

       - 애플리케이션 로딩 시, @Query 어노테이션에 입력된 정적 쿼리문을 파싱해서 문법 오류가 있는지 검사함
       - 정적 쿼리인 경우에 실무에서 가장 많이 쓰는 방법
       - 동적 쿼리는 QueryDsl을 가장 많이 사용함

   - @Query, 값, DTO 조회하기

     - 코드

       - ```java
         public interface MemberRepository extends JpaRepository<Member, Long> {
             @Query("select m.username from Member m")
             List<String> findUsernameList();
         
             @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
             List<MemberDto> findMemberDto();
         }
         ```

     - TIP

       - `QueryDsl을 쓰면 이 방법은 쓰지않음`

   - 파라미터 바인딩

     - 코드

       - ```java
         select m from Member m where m.username = ?0 //위치 기반
         select m from Member m where m.username = :name //이름 기반
         ```

       - `현재는 실무에서 '위치' 기반은 거의 쓰지 않음`

       - 코드  가독성이나 유지보수 측면에서 '이름' 기반이 더 뛰어남. 왜냐하면 '위기 기반'은 위치가 바뀌면 오류가 날 확률이 매우 큼. 

     - 컬렉션 파라미터 바인딩

       - ```java
         @Query("select m from Member m where m.username in :names")
         List<Member> findByNames(@Param("names") List<String> names); // (4)파라미터 바인딩(컬렉션 파라미터 바인딩, 이름기반)
         
         @Query("select m from Member m where m.username in :names")
         List<Member> findByNames(@Param("names") Collection<String> names); // 다양한 input을 위해 List에서 Collection으로 바꿈
         ```

   - 반환 타입

     - 컬렉션
       - 컬렉션 조회 시, 해당 컬렉션이 empty 일 때 스프링 데이터 JPA가 알아서 `빈 컬렉션을 생성해서 반환`
       - NPE가 발생하지 않음
       - 순수한 JPA가 아니라 스프링 데이터 JPA이기 떄문
     - 단건
       - 단건 조회 시, 해당 단건이 없을 때 그냥 `null을 반환`
       - 단건 조회 시, 결과가 2건 이상이면 `NonUniqueResultException ` 발생
       - NoResultException이 발생하지 않음
       - 순수한 JPA가 아니라 스프링 데이터 JPA이기 떄문
     - Optional
       - DB에 해당 데이터가 있을지 없을지 모른다면 꼭 Optional을 쓰는 것을 추천
     - 테스트 사진
       - ![image-20231220022036745](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/8da6bf01-ac87-4538-8d27-e7200ee6cee1)
     - [스프링 데이터 JPA 반환타입 레퍼런스](https://docs.spring.io/spring-data/jpa/reference/#repository-query-return-types)

   - 순수 JPA 페이징과 정렬

     - 코드

       - ```java
         public List<Member> findByPage(int age, int offset, int limit){
         return entityManager.createQuery("select m from Member m where m.age = :age order by m.username desc")
                 .setParameter("age" ,age)
                 .setFirstResult(offset)
                 .setMaxResults(limit)
                 .getResultList();
         }
         
         public long totalCount(int age){
         return entityManager.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                 .setParameter("age", age)
                 .getSingleResult();
         }
         ```

     - application.yml

       - limit, offset 쿼리를 오라클 형식으로 보고싶을 때, `dialect`를 한 줄 추가

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
                 format_sql: true
                 dialect: org.hibernate.dialect.Oracle10gDialect #추가
         
         ```

   - 스프링 데이터 JPA 페이징과 정렬

     - 페이징과 정렬 파라미터

       - `org.springframework.data.domain.Sort`
         - 정렬 기능
       - `org.springframework.data.domain.Pageable`
         - 페이징 기능(내부에 Sort 포함)
       - TIP
         - 관계형DB든 NoSQL이든 `스프링 데이터 JPA`를 통해 페이징/정렬 기능을 인터페이스로 공통화해서 구현함
         - 스프링 데이터 JPA는 첫 번째 페이지를 0부터 시작

     - 특별한 반환 타입

       - `org.springframework.data.domain.Page`

         - 총 개수(count)를 결과에 포함해서, 출력하는 페이징 기능

       - `org.springframework.data.domain.Slice`

         - 총 개수(count)를 결과에 포함하지 않고, 출력하는 페이징 기능

           - ```tex
             Slice는 전체 페이지의 데이터 크기, 
             전체 페이지 개수를 확인하는 메소드가 없음(totalCount 쿼리를 호출하지 않기 때문)
             
             slice.getTotalElements().isEqualTo(5); // X
             slice.getTotalPages().isEqualTo(2); // X
             ```

         - 모바일 같은 경우, 사용자가 스크롤을 내릴 때마다 새로운 페이지를 반환해야 한다면 굳이 총 개수(count)를 알 필요 없음

         - ```sql
           /* 내부적으로 limit + 1조회 */
           select 
           	member_id as member_i1_0_
           	, member0_.age as age2_0_
           	, member0_.team_id as team_id4_0_
           	, member0_.username as username3_0_ 
           from member member0_ 
           where member0_.age=10 
           order by member0_.username desc 
           limit 4; /* '3'이 나와야하는데 슬라이스는 limit + 1 이므로 '4'가 출력*/
           ```

       - `List`

         - 총 개수(count) 쿼리 없이 결과만 반환

     - 주의

       - SQL문이 복잡해지면 COUNT 쿼리를 따로 작성하는 것이 좋음(단순한 SQL문이면 따로 COUNT문을 따로 선언 X)

       - ```java
         @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
         Page<Member> findByAge(int age, Pageable pageable);
         ```

       - 정렬도 너무 복잡해지면 `PageRequest.of()`에 넣지말고 `@Query`에 작성하자

   - 벌크성 수정 쿼리

     - 개념

       - 한 개씩 update문을 처리하는게 아니라 한번에 update문을 처리할 때 쓰는 SQL문을 벌크성 수정 쿼리라고 함

     - 순수 JPA

       - ```java
         public int bulkAgePlus(int age) {
             return entityManager.createQuery(
                 "update Member m set m.age = m.age + 1 " +
                 "where m.age >= :age")
                 .setParameter("age", age)
                 .executeUpdate();
         }
         ```

     - 스프링 데이터 JPA

       - ```java
         @Modifying(clearAutomatically = true) //해당 어노테이션이 있어야 .executeUpdate() 같은 역할을 함
         @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
         int bulkAgeUpdate(@Param("age") int age);
         ```

       - `@Modifying`가 없다면 update문이 list나 single result를 호출함

     - 공통 문제

       - 순수 JPA든 스프링 데이터 JPA든 bulk성 update는 엔티티의 영속성 생애주기를 무시하고, 직접 DB에 쿼리를 날림
       - 그래서 영속성 컨텍스트 안에 있는 엔티티 정보는 DB와는 다른 이전 정보를 가지고 있기 때문에
       - DB에는 bulk성 수정 SQL문으로 41살이지만, 영속성 컨텍스트에서는 아직 40살이기 때문에 오류 발생

     - 해결

       - DB와 영속성 컨텍스트의 데이터 차이를 해결하기 위해서 `EntityManager`의 `flush()`, `clear()`를 이용한다

       - ```java
         entityManager.flush(); // DB와 영속성 컨텍스트의 1차 캐시 데이터 사이에 차이가 발생하면 동기화 시켜주는 메소드, 벌크성 수정을 영속성 컨텍스트에서도 반영하기 위함
         entityManager.clear(); // 만약 해당 작업을 안 하고싶다면 @Modifying(clearAutomatically = true) 작성
         ```

       - 물론 벌크성 수정처리 이후에 API가 종료되거나 다른 트랜잭션에서 비즈니스 로직이 처리되는거면 상관없음

       - 하지만 벌크성 수정처리가 발생한 같은 트랜잭션에서 다른 로직이 처리된다면 오류가 발생할 가능성이 큼

       - JPA를 MyBatis, JDBC 템플릿 등과 같이 쓸 때, SQL mapper가 DB에 직접 SQL문을 처리하는걸 JPA가 인식하지 못 하므로 DB와 영속성 컨텍스트의 데이터가 차이날 수 있다. 그러므로 이때는 clear(), flush()를 하는걸 추천 

   - @EntityGraph

     - **TODO**

       - Fetch Join에 대해 모르면 실무에서 JPA를 사용하긴 힘듦, 잘 알아두자

     - 프록시 객체

       - 지연 로딩(fetch = FetchType.LAZY)으로 설정된 엔티티는 실제로 호출하기 전까지는 DB에서 조회하지 않음
       - 이때 영속성 컨텍스트에 없는 엔티티를 호출하면 JPA가 사용자에게 프록시 객체를 전달하고, 실제 해당 엔티티를 사용하면 DB에서 조회해서 영속성 컨텍스트로 가져옴
       - ![image-20231228174254118](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/ded475c3-8878-4ace-bdba-bbfb10ec7330)

     - N + 1

       - 테스트 코드에서 Member 리스트를 1번 조회했는데 실제로는 Team 조회 쿼리 2개까지 총 3번 출력
         -  Member 1번 조회 + Team N(2)번 조회

     - N + 1 해결방법 1

       - Fetch Join

         - Member를 조회할 때, Member와 연관관계인 엔티티도 모두 한번에 가져와서 프록시가 아닌 진짜 엔티티 정보를 조회

         - ```java
           @Query("select m from Member m left join fetch m.team")
           List<Member> findMemberFetchJoin();
           ```

         - ![image-20231228180021839](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/16ee0942-8893-493e-bdb6-a0e39b929cbd)

     - N + 1 해결방법 2

       - @EntityGraph

         - ```java
           @Override
           @EntityGraph(attributePaths = {"team"}) // Member 조회 시, 연관된 엔티티 중에 Fetch Join으로 한번에 가져올 엔티티를 선언하는 어노테이션
           List<Member> findAll();
           
           @Query("select m from Member m")
           @EntityGraph(attributePaths = {"team"})
           List<Member> findMemberEntityGraph();
           
           @EntityGraph(attributePaths = {"team"})
           List<Member> findEntityGraphByUsername(@Param("username") String username);
           ```

     - TIP

       - 스프링 데이터 JPA를 알기보단 JPA를 알면 문제 상황을 더 빨리 해결할 수 있으므로 JPA 도서를 함 읽어보기

   - JPA Hint & Lock

     - 개념

       - JPA 구현체에게 제공하는 힌트를 의미
       - **JPA에서 제공하는게 아닌 Hibernate에서 제공함**

     - 상황

       - Dirty checking할 때, 원본과 사본 데이터(스냅샷) 2개 가지고 변경된 점을 인지하고 수정함.
       - 문제는 단순 조회 때도 데이터 2개를 사용하기 때문에 메모리 낭비가 발생함
       - 이를 해결하기 위해 Hibernate에서 제공하는 JPA Hint를 사용할 수 있음

     - 코드

       - ```java
         @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
         Member findReadOnlyByUsername(String username);
         ```

     - JPA hint TIP

       - `@QueryHint(name = "org.hibernate.readOnly", value = "true")`로 모든 조회 API에 옵션을 넣어줘도 극적인 성능최적화 효과를 기대하기는 어려움 
       - 실무에서의 대부분 성능이 안 나오는 DB 문제는 복잡한 SQL문이 비효율적으로 처리되는 등의 문제임
       - 그쯤 되면 readOnly 옵션으로 해결할게 아니라 해당 SQL문을 수정하든가 아님 DB에 캐시를 설치해서 처리속도를 높여야함

     - JPA lock TIP

       - 실시간 트래픽이 많은 서비스에서는 가급적이면 LOCK을 걸지 않거나 굳이 걸어야 하면 Optimistic Lock을 추천

     - **TODO**

       - JPA책 마지막에 보면 JPA의 Lock의 레벨에 따른 설명이 자세하게 나와있음 추후에 읽고 정리하기
       
         

5. 확장 기능

   - 사용자 정의 repository 구현

     - 개요

       - 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
       - 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
       - 근데 인터페이스의 메소드를 직접 구현하고 싶다면?
         - 기본적인 메소드는 인터페이스에 있는걸 쓰고, 복잡한 쿼리의 90%는  QueryDsl로, 나머지 10%는 가끔 SpringJdbcTemplate/MyBatis을 사용

     - 유의점

       - 사용자 정의 repository 생성하려면 `리포지토리 인터페이스 이름 + Impl`라는 규칙을 반드시 지켜야한다. 그래야 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록함

       - ```java
         public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
         }
         
         // MemberRepository + Impl = 해당 인터페이스와 맞춤
         public class MemberRepositoryImpl implements MemberRepositoryCustom{
         }
         ```

     - TIP

       - ```
         항상 사용자 정의 리포지토리가 필요한 것은 아니다. 그냥 임의의 리포지토리를 만들어도 된다. 
         
         예를 들어 MemberQueryRepository를 인터페이스가 아닌 클래스로 만들고 스프링 빈으로 등록해서 그냥 직접 사용해도 된다. 
         
         물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다
         ```

       - SW 개발 시, 하나의 방법만 믿고 쓰는 방법은 매우 좋지 않은 습관이므로 다양한 방법 중에서 가장 유지/보수가 좋은 방법을 선택하는 개발자가 되도록 노력하자

       - 핵심 비즈니스 로직이 아닌 화면에 맞춘 사용자 정의 repoisitory(인터페이스)를 구현할 때, 인터페이스를 implements해서 무한정 확장하지 말고 새로운 repository를 만들어서 분리하는 것을 추천

         - repository를 너무 많이 implements하면 의존성이 너무 커짐

   - Auditing

     - 엔티티를 생성, 변경할 때 필수 정보

       - 등록자
       - 등록일시
       - 수정자
       - 수정일시

     - 순수 JPA 방법

       - ```java
         @Getter
         @MappedSuperclass //해당 어노테이션이 있어야 JpaBaseEntity를 상속받은 다른 엔티티가 해당 칼럼(생성/수정일시)을 사용할 수 있음
         public class JpaBaseEntity {
         
             @Column(updatable = false)
             private LocalDateTime createdDate;
             private LocalDateTime updatedDate;
         
             @PrePersist
             public void prePersist(){
                 LocalDateTime now = LocalDateTime.now();
                 this.createdDate = now;
                 this.updatedDate = now; //최소 생성시 수정일시는 생성일시와 동일, null로 두지않음
             }
         }
         
         @Entity
         public class Member extends JpaBaseEntity{
         
             @Id
             @GeneratedValue
             @Column(name = "member_id")
             private Long id;
             private String username;
             private int age;
         }
         ```

     - 스프링 데이터 JPA 방법

       - ```java
         //@EnableJpaAuditing(modifyOnCreate = false) //해당 옵션 사용 시, 처음에 update값이 처음에는 null로 들어감(create 값은 그대로)
         @EnableJpaAuditing //해당 어노테이션을 선언해야 BaseEntity를 인식함(스프링 데이터 JPA)
         @SpringBootApplication
         public class DataJpaApplication {
         
             public static void main(String[] args) {
                 SpringApplication.run(DataJpaApplication.class, args);
             }
         
             @Bean
             public AuditorAware<String> auditorProvider() {
                 return () -> Optional.of(UUID.randomUUID().toString());
             }
         
         //    @Bean //해당 익명 메소드를 람다로 바꾼 것
         //    public AuditorAware<String> auditorProvider() {
         //        return new AuditorAware<String>() {
         //            @Override
         //            public Optional<String> getCurrentAuditor() {
         //                return Optional.of(UUID.randomUUID().toString());
         //            }
         //        };
         //    }
             
         }
         
         @EntityListeners(AuditingEntityListener.class) //해당 어노테이션이 있어야 데이터의 변경(C/U/D) 시 이벤트(@PreUpdated...)를 발생시킨다
         @Getter
         @MappedSuperclass
         public class BaseEntity {
         
             @CreatedDate
             @Column(updatable = false)
             LocalDateTime createdDate;
         
             @LastModifiedDate
             LocalDateTime lastModifiedDate;
         
             @CreatedBy
             @Column(updatable = false)
             String createdBy; //등록자
         
             @LastModifiedBy
             String lastModifiedBy; //수정자
         }
         
         @Entity
         public class Member extends BaseEntity {
         
         }
         ```
       
       - `DataJpaApplication` run 클래스에 @EnableJpaAuditing를 등록 시, 세션 정보나 스프링 시큐리티 로그인 정보에서 ID를 받을 수 있다
       
     - 전체 적용

       - `@EntityListeners(AuditingEntityListener.class) `를 엔티티에 선언하는 것을 생략하고 스프링 데이터 JPA가 제공하는 이벤트를 엔티티 전체에 적용하려면 orm.xml에 다음과 같이 등록하면 된다.

       - ```xml
         <!-- META-INF/orm.xml -->
         <?xml version="1.0" encoding="UTF-8"?>
         <entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd" version="2.2">
          	<persistence-unit-metadata>
          		<persistence-unit-defaults>
          			<entity-listeners>
          				<entity-listener class="org.springframework.data.jpa.domain.support.AuditingEntityListener"/>
          			</entity-listeners>
          		</persistence-unit-defaults>
          	</persistence-unit-metadata>
         </entity-mappings>
         ```

   - Web 확장 - 도메인 클래스 컨버터

     - 개념

       - HTTP 파라미터로 넘어온 엔티티의 id로 엔티티 객체를 찾아서 바인딩함

     - 코드

       - ```java
         /* 적용 전 */
         @GetMapping("/members/v1/{id}")
         public String findMemberV1(@PathVariable("id") Long id){
             Member member = memberRepository.findById(id).orElse(new Member("John Doe"));
             return member.getUsername();
         }
         
         /* 적용 후 */
         @GetMapping("/members/v2/{id}") //도메인 클래스 컨버터 - 실무에서 굳이 추천하지 않음
         public String findMemberV2(@PathVariable("id") Member member){
             return member.getUsername();
         }
         ```

       - HTTP 요청은 회원 id 를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환

       - 도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음

     - 주의

       - 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 `단순 조회용으로만 사용`해야 함 
       - 즉 트랜잭션이 없는 범위에서 엔티티를 조회했으므로, `엔티티를 변경해도 DB에 반영되지 않음`

   - Web 확장 - 페이징과 정렬

     - 메소드

       - ```java
         @GetMapping("/members")
         public Page<Member> list(Pageable pageable) { //파라미터에 Pageable를 넣는 순간 스프링부트가 알아서 data binding 처리
             return memberRepository.findAll(pageable);
         }
         ```

     - 글로벌 페이징 설정

       - ```yaml
         spring:
           data:
             web:
               pageable:
                 default-page-size: 10 # 기본 한 페이지 개수(미설정 시 자동으로 20개)
                 max-page-size: 2000 # 한 페이지 최대 데이터건수 2000개
                 one-indexed-parameters: true # 페이지의 기본 시작 index를 0이 아닌 1부터 시작하는 옵션
         ```

     - 특정 메소드 페이징 설정

       - ```java
         @GetMapping("/members")
         public Page<Member> listV1(@PageableDefault(size = 5, sort = {"id", "username"}) Pageable pageable) { //파라미터에 Pageable를 넣는 순간 스프링부트가 알아서 data binding 처리
             return memberRepository.findAll(pageable);
         }
         
         @GetMapping("/members") //DTO로 변환함
         public Page<MemberDto> listV2(@PageableDefault(size = 5, sort = {"id", "username"}) Pageable pageable) {
             Page<Member> page = memberRepository.findAll(pageable);
             return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
         }
         
         @GetMapping("/members") //DTO로 변환 시, 즉시 converter로 쓸 수 있게 MemberDto 생성자 수정
         public Page<MemberDto> listV3(@PageableDefault(size = 5, sort = {"id", "username"}) Pageable pageable) {
             Page<Member> page = memberRepository.findAll(pageable);
             return page.map(member -> new MemberDto(member));
         }
         ```

6. 스프링 데이터 JPA 분석

   - 스프링 데이터 JPA 구현체 분석

     - **TODO: SOAP vs REST API**

     - SimpleJpaRepository

       - 경로
         - ![image-20240115102850983](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/917b0517-13c7-44ba-b160-4a8bb4a1316f)
         - ![image-20240115102915850](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/07409ba8-43c5-44b0-ae66-ef79eb914c05)

     - [@Repository](https://docs.spring.io/spring-framework/docs/5.3.30/javadoc-api/org/springframework/stereotype/Repository.html)

       - 효과

         - 어노테이션이 선언된 `해당 클래스를 Spring Bean의 Component scan 대상에 올리기`

         -  JPA, JDBC등 다양한 데이터 영속성 계층에서 예외처리가 발생하면 JPA나 JDBC에서 고유의 예외처리를 `스프링 프레임워크에서 쓰는 예외처리로 변환함`

           - 즉 데이터 접근을 JDBC에서 JPA로 바꾼다 해도 예외처리를 실행하는 메커니즘은 같기 때문에 상관없음

         - ```
           A class thus annotated is eligible for Spring DataAccessException translation when used in conjunction with a PersistenceExceptionTranslationPostProcessor. The annotated class is also clarified as to its role in the overall application architecture for the purpose of tooling, aspects, etc.
           
           https://goodteacher.tistory.com/698
           
           /* JPA 예외를 스프링 프레임워크가 추상화한 예외로 변환함 */
           ```

     - @Tranactional

       - 코드

         - ```java
           @Repository
           @Transactional(
               readOnly = true
           )
           public class SimpleJpaRepository<T, ID> implements JpaRepositoryImplementation<T, ID> {
               /**
               * 'readOnly = true'시 EntityManager의 flush() 남용을 막을 수 있어서 조회 성능에 약간의 향상
               /
           }
           ```

         - Tranaction이 완료되면 JPA에서 flush()를 호출하는데 `readOnly = true`면 flush()를 호출하지 않음 ==> 변경감지가 일어나지 않음 ==> 조회는 데이터를 변경하지 않음

       - 우선순위

         - ```
           클래스 메소드 -> 클래스 -> 인터페이스 메소드 -> 인터페이스
           
           클래스에 'readOnly=true'라고 해도, 해당 클래스의 메소드에 'readOnly=false'를 작성하면 false가 적용
           ```

     - save() 메소드

       - 코드

         - ```java
           /**
            * 파라미터로 들어온 Entity가
            * 1)새로운 Entity라면 persist()
            * 2)기존 Entity라면 merge()
            */
           @Transactional
           public <S extends T> S save(S entity) {
               Assert.notNull(entity, "Entity must not be null.");
               if (this.entityInformation.isNew(entity)) {
                   this.em.persist(entity);
                   return entity;
               } else {
                   return this.em.merge(entity);
               }
           }
           ```

       - 유의점

         - 대부분의 상황에서 JPA의 변경감지를 통해서 Entity를 수정하는 것을 매우 권장
         - merge()는 비영속 상태의 Entity를 다시 영속 상태의 Entity로 만들 때 쓰는 것

   - 새로운 Entity를 구별하는 방법

     - 새로운 Entity를 판단하는 기본 전략

       - 식별자가 객체일 때 `null` 로 판단
       - 식별자가 자바 원시 타입(`primitive type: int, boolean, ...`)일 때 `0` 으로 판단
         - int id = null, char id = null처럼 원시 타입은  객체가 아니라서 null 선언이 불가함 ==> `0`으로 판단 ==> 새로운 Entity로 인식
       - `Persistable` 인터페이스를 구현해서 판단 로직 변경 가능

     - 실제로 null인가

       - persist() 호출 전
         - ![image-20240115152815133](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/1a730c66-aa7c-44f6-b4f0-30c7493873eb)
       - persist() 호출 후
         - ![image-20240115153111949](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/3f2cee6c-1e84-4481-b77d-bb2d47162bb8)

     - @GeneratedValue가 없을 때 오류

       - Item 코드

         - ```java
           @Entity
           @Getter
           @NoArgsConstructor(access = AccessLevel.PROTECTED)
           public class Item {
               @Id
           //    @GeneratedValue
               private Long id;
           
               private String itemName;
           
               public Item(Long id){
                   this.id = id;
               }
           }
           ```

       - 분명히 Entity의 id를 설정했는데 실제로 insert문이 날아갈 때는 null로 설정

         - ![image-20240115155422019](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/e612944f-40c4-449b-83b6-1b73337d8ca8)

       - 이유

         - SimpleJpaRepository 클래스에서 save() 메소드를 호출할 때 `if (this.entityInformation.isNew(entity))`라는 조건문이 존재
         - 해당 조건문 때문에 새로운 Entity로 인식해야하지만 그렇지 못 하기 때문에 merge()로 처리
         - ![image-20240115155451184](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/d0a3dd90-5b59-47e4-8c04-5ec195f999cc)

     - 해결

       - save()보다는 persist()를 애용하자

       - 코드
     
         - ```java
           @Entity
           @NoArgsConstructor(access = AccessLevel.PROTECTED)
           public class Item extends BaseTimeEntity implements Persistable<String> {
               @Id
               private String id;
           
           //    @CreatedDate
           //    private LocalDateTime createdDate; //BaseTimeEntity를 상속받지 않는다면 해당 코드와 @EntityListeners(AuditingEntityListener.class)를 추가하자
           
               private String itemName;
           
               public Item(String id) {
                   this.id = id;
               }
           
               @Override
               public String getId() {
                   return null;
               }
           
               @Override //오버라이드를 통해 새로운 조건으로 변경
               public boolean isNew() {
                   return this.createdDate == null;
               }
           }
           ```
     
     - 참고
     
       - ```
         JPA 식별자 생성 전략이 `@GenerateValue` 면 `save()` 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작한다. 
         
         그런데 JPA 식별자 생성 전략이 `@Id` 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 `save()` 를 호출한다. 
         
         따라서 이 경우 `merge()` 가 호출된다. `merge()` 는 우선 DB를 호출해서 값을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다. 
         
         따라서 `Persistable` 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.
         
         참고로 등록시간( `@CreatedDate` )을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수
         있다. (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)
         ```
     
         

7. 나머지 기능들 - 실무에서 잘 쓰이진 않음

   - Specifications(명세)
   
     - 유의점
   
       - 스프링 데이터 JPA는 JPA Criteria를 활용해서 이 개념을 사용할 수 있도록 지원하지만 실무에서 JPA Criteria를 사용하는 경우는 거의 없음
       -  **실무에서는 JPA Criteria를 거의 안 씀! 대신에 QueryDSL을 사용하자.**
   
     - 술어 코드
   
       - ```java
         public class MemberSpecifications {
         
             public static Specification<Member> teamName(final String teamName) {
                 return new Specification<Member>() {
                     @Override
                     public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
         
                         if (!StringUtils.hasText(teamName)) {
                             return null;
                         }
         
                         //root는 엔티티를 의미
                         Join<Member, Team> t = root.join("team", JoinType.INNER); //회원과 팀 조인
                         return criteriaBuilder.equal(t.get("name"), teamName); //where문 작성: 팀명이 파라미터로 들어온 'teamName'과 같아야함
                     }
                 };
             }
         }
         
         /* 아래 코드는 람다로 개선한 코드 */
         public class MemberSpecifications {
         
             public static Specification<Member> teamName(final String teamName) {
                 return (root, query, criteriaBuilder) -> {
         
                     if (!StringUtils.hasText(teamName)) {
                         return null;
                     }
                     
                     Join<Member, Team> t = root.join("team", JoinType.INNER); //회원과 팀 조인
                     return criteriaBuilder.equal(t.get("name"), teamName); //where문 작성: 팀명이 파라미터로 들어온 'teamName'과 같아야함
                 };
             }
         }
         
         /* 명세를 정의하려면 Specification 인터페이스와 toPredicate(...) 메서드만 구현하면 되는데 JPA Criteria의 Root ,CriteriaQuery , CriteriaBuilder 클래스를 파라미터 제공함 */
         ```
     
   - [Query by sample](https://docs.spring.io/spring-data/jpa/reference/data-commons/query-by-example.html)
   
     - 유의점
   
       - **매칭 조건이 너무 단순하고, 내부 조인만 가능하기 때문에 실무에서는 Query by sample를 거의 안 씀! 대신에 QueryDSL을 사용하자.**
   
     - 코드
   
       - ```java
         @Test
         @DisplayName("Query Sample 테스트")
         public void querySample() {
             //given
             Team team = new Team("Arsenal");
             entityManager.persist(team);
         
             Member memberA = new Member("jaeuk", 0, team);
             Member memberB = new Member("minyoung", 0, team);
             entityManager.persist(memberA);
             entityManager.persist(memberB);
         
             entityManager.flush();
             entityManager.clear();
         
             //when
             Member member = new Member("jaeuk"); //Probe 생성, Probe: 필드에 데이터가 있는 실제 도메인 객체
             ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnorePaths("age"); //age 칼럼을 무시
             Example<Member> example = Example.of(member, exampleMatcher);
             List<Member> result = memberRepository.findAll(example);
         
             //then
             assertThat(result.get(0).getUsername()).isEqualTo("jaeuk");
         }
         ```
   
     - 장점
   
       - 동적 쿼리를 편리하게 처리
       - 도메인 객체를 그대로 사용
       - 데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경이 없게 추상화 되어 있음
       - 스프링 데이터 JPA `JpaRepository` 인터페이스에 이미 포함
   
     - 단점
   
       - 외부 조인 불가능(INNER JOIN만 가능)
       - 중첩 제약 조건 불가능
         - `firstname = ?0 or (firstname = ?1 and lastname =?2)`
       - 매칭 조건이 매우 단순함
         - 문자는 `starts/contains/ends/regx`
         - 다른 속성은 `=`만 지원
   
   - [Projections](https://docs.spring.io/spring-data/elasticsearch/reference/data-commons/repositories/projections.html)
   
     - 개념
       - 전체 Entity가 아닌 Entity의 특정 property만 조회하고 싶다면 projection을 이용함
       - 프록시 객체로 처리
       - ![image-20240117142730464](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/3b93f125-9b47-470a-9b8d-db3bb01879e1)
       
     - Closed Projection
   
       - 프로젝션 인터페이스의 메소드가 엔티티의 프로퍼티 중 조회하고 싶은 것만 선별하는 프로젝션을 의미
       - Spring Data가 쿼리를 최적화함
   
     - Open Projection
   
       - `@Value` 어노테이션을 사용한 projection
       - Entity의 모든 property를 조회한 후 원하는 property만 이용하는 projection
   
     - 클래스 기반 Projection
   
       - 실제 객체로 처리
       - ![image-20240117142604223](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/d48ccb8f-bb7e-4729-9a70-c2aed5aa6de9)
   
     - 동적 Projection
   
       - 코드
   
       - ```java
         <T> List<T> findClassProjectionsByUsername(@Param("username") String username, Class<T> type);
         ```
   
     - 중첩 구조 처리
   
       - 코드
   
       - ```java
         public interface NestedClosedProjection {
         
             String getUsername(); //root이기 때문에 정확하게 잘 가져옴
         
             TeamInfo getTeam(); //root가 아니기 때문에 LEFT OUTER JOIN 처리
         
             interface TeamInfo{
                 String getName();
             }
         }
         
         /* 아래는 테스트코드 */
         List<NestedClosedProjection> resultC = memberRepository.findClassProjectionsByUsername("jaeuk", NestedClosedProjection.class);
         for(NestedClosedProjection nestedClosedProjection : resultC){
             System.out.println("username ======= " + nestedClosedProjection.getUsername());
             System.out.println("teamName ======= " + nestedClosedProjection.getTeam().getName());
         }
         ```
       
       - ![image-20240117145105183](https://github.com/wooko5/Spring-Data-JPA/assets/58154633/732147e7-f80d-4e66-8a17-e152e7fcce20)
       
     - 주의
     
       - 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화가 가능
       - **프로젝션 대상이 ROOT가 아니면**
         - **LEFT OUTER JOIN 처리**
         - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
     
     - 정리
     
       - 프로젝션 대상이 root 엔티티면 유용
       - 하지만 root 엔티티가 아닌 엔티티는 select절 최적화를 못 해서 원하는 property만 조회하는게 힘듦(전체 조회 후 원하는 property만 재조회)
       - 실무의 복잡한 query를 해결하기에는 한계점이 존재
   
   
   - Native Query
   
     - 개념
   
       - SQL을 그대로 문자열로 만들어서 사용하는 방법
       - **가급적 사용하지 않는 것을 추천, 정말 어쩔 없을 때는 projection을 이용**
   
     - 페이징 지원
   
     - 반환타입
   
     - 제약
   
       - Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음
       - JPQL처럼 컴파일 단계에서 문법확인 불가하고, 런타임 단계에서 오류 확인가능해서 위험함
       - 동적 쿼리 불가능
         - 하이버네이트를 직접 활용하거나
         - 스프링 JDBC 템플릿, myBatis같은 외부 라이브러리 사용하기
   
     - 코드
   
       - ```java
         @Query(value = "select * from member where username = ?", nativeQuery = true)
         Member findByNativeQuery(String username);
         ```

