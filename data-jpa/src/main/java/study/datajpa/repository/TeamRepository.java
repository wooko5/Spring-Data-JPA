package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // 구현체가 없는 interface인데 JPA가 알아서 구현 클래스를 생성함
    // @Repository 생략가능
    // 컴포넌트 스캔을 스프링 데이터 JPA가 알아서 처리
    // JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리
}
