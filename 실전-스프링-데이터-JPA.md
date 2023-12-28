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
         entityManager.flush(); // DB와 영속성 컨텍스트의 데이터 중에 차이가 발생하면 동기화 시켜주는 메소드, 벌크성 수정을 영속성 컨텍스트에서도 반영하기 위함
         entityManager.clear(); // 만약 해당 작업을 안 하고싶다면 @Modifying(clearAutomatically = true) 작성
         ```

       - 물론 벌크성 수정처리 이후에 API가 종료되거나 다른 트랜잭션에서 비즈니스 로직이 처리되는거면 상관없음

       - 하지만 벌크성 수정처리가 발생한 같은 트랜잭션에서 다른 로직이 처리된다면 오류가 발생할 가능성이 큼

       - JPA를 MyBatis, JDBC 템플릿 등과 같이 쓸 때, SQL mapper가 DB에 직접 SQL문을 처리하는걸 JPA가 인식하지 못 하므로 DB와 영속성 컨텍스트의 데이터가 차이날 수 있다. 그러므로 clear(), flush()를 하는걸 추천 

   - @EntityGraph

     - TODO

       - Fetch Join에 대해 모르면 실무에서 JPA를 사용하긴 힘듦, 잘 알아두자

     - 프록시 객체

       - 지연 로딩(fetch = FetchType.LAZY)으로 설정된 엔티티는 실제로 호출하기 전까지는 DB에서 조회하지 않음
       - 이때 영속성 컨텍스트에 없는 엔티티를 호출하면 JPA가 사용자에게 프록시 객체를 전달하고, 실제 해당 엔티티를 사용하면 DB에서 조회해서 영속성 컨텍스트로 가져옴
       - ![image-20231228174254118](C:\Users\wooko\AppData\Roaming\Typora\typora-user-images\image-20231228174254118.png)

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

         - ![image-20231228180021839](C:\Users\wooko\AppData\Roaming\Typora\typora-user-images\image-20231228180021839.png)

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

5. 확장 기능

6. 스프링 데이터 JPA 분석

7. 나머지 기능들
