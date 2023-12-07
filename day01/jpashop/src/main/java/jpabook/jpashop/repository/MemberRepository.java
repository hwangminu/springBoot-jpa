package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
// FINAL 이 붙은
public class MemberRepository {

   //  @PersistenceContext // EntityManagerFactory의 역할을 어노테이션이 대신한다 => build에서 spring-data-jpa와 application.yml을 통해서 팩토리 설정이 완료된다
    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    // 단건 조회
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    // 이름으로 조회
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member  m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }


}


// 테스트 생성 : ctrl + shift + t
