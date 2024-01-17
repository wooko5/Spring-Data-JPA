package study.datajpa.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.criteria.*;

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

    public static Specification<Member> username(final String username){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
    }
}
