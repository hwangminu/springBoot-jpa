package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    // Custom Interface 를 구현하는 클래스는 레포지토리 이름 뒤에 impl을 붙힌다

    private final EntityManager em;

    @Override
    public List<Member> findmemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
