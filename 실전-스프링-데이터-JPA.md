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

       - ![image-20231121003614138](C:\Users\wooko\AppData\Roaming\Typora\typora-user-images\image-20231121003614138.png)

     - annotation processors 설정 변경

       - ![image-20231121003547838](C:\Users\wooko\AppData\Roaming\Typora\typora-user-images\image-20231121003547838.png)

   - 라이브러리

     - 라이브러리간의 의존성 확인
       - `./gradlew dependencies --configuration compileClasspath`
       - ![image-20231121003949148](C:\Users\wooko\AppData\Roaming\Typora\typora-user-images\image-20231121003949148.png)
     - TIP
       - 스프링부트 2.0부터는 디폴트 Connection Pool(CP)가 Hikari CP임
       - 이전에는 Tomcat-JDBC를 사용, Hikari CP의 성능이 더 좋기 떄문에 바뀜
       - 빠른 이유: 

   - H2 DB 생성

     - [H2 설치경로](https://www.h2database.com/)
     - [스프링부트 프로젝트 가이드](https://spring.io/projects)
       - [스프링부트 해당 버전의 개요](https://spring.io/projects/spring-boot#learn)
       - [스프링부트 해당 버전의 적합한 라이브러리 의존성 버젼](https://docs.spring.io/spring-boot/docs/2.7.17/reference/html/dependency-versions.html#appendix.dependency-versions)
     -  최초 실행 시
       - ![image-20231121011420165](C:\Users\wooko\AppData\Roaming\Typora\typora-user-images\image-20231121011420165.png)
       - ~/datajpa.mv.db 파일 생성 확인
       - 위처럼 접속하면 파일에 lock이 걸려서 다른 곳에서 접근이 불가하니 아래처럼 수정하자
     - 이후 실행 시
       - ![image-20231121011536737](C:\Users\wooko\AppData\Roaming\Typora\typora-user-images\image-20231121011536737.png)

   - 스프링 데이터 JPA와 DB 설정, 동작 확인

2. 예제 도메인 모델

3. 공통 인터페이스 기능

4. 쿼리 메소드 기능

5. 확장 기능

6. 스프링 데이터 JPA 분석

7. 나머지 기능들
