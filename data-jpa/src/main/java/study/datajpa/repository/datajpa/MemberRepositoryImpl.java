package study.datajpa.repository.datajpa;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager entityManager;

    //JPA 직접 사용(EntityManager)
    @Override
    public List<Member> findMemberCustom() {
        return entityManager.createQuery("select m from Member m").getResultList();
    }
}
