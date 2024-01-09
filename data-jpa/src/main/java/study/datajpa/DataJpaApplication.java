package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

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