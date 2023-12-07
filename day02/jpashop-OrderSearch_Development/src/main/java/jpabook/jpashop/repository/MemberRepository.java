package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository // 스프링 빈으로 등록, JPA 예외를 스프링 기반 예외로 예외 변환
public class MemberRepository {

//    @PersistenceUnit    // 엔티티 매니저 팩토리(EntityMangerFactory) 주입
//    private EntityManagerFactory emf;
    @PersistenceContext // 엔티티 매니저(EntityManger) 주입, EntityMangerFactory의 역할을 대신한다
    // 스프링 데이터 JPA와 lombok을 사용하면 EntityManger도 @RequiredArgsConstructor로 주입이 가능하다
    private EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", name).getResultList();
    }
}
