package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 주문 리포지토리에는 주문 엔티티를 저장하고 검색하는 기능이 있다.
    public List<Order> findAll(OrderSearch orderSearch) {
        // repository 패키지에 OrderSearch라는 클래스를 생성하고 진행한다
        // 1. 무식한 방법 : JPQL 문자를 생짜로 작성한다
        // - 검색 조건이 무조건 들어온다고 가정하고 작성

        // 기본적으로 작성하는 쿼리
        return em.createQuery("select o from Order o join o.member m where o.status = :status and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)
                .getResultList();

        // 만약 status와 name이 null일 때 모든 정보를 가져오려면 동적 쿼리를 작성해야 한다
//        String jpql = "select o from Order o join o.member m";
//        return em.createQuery(jpql, Order.class).setMaxResults(1000).getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        // 2. JPQL로 동적 쿼리 작성

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        // 2. JPA Criteria로 작성
        // - 실무에서 사용 안함
        // - 이런게 있다고 보여주기 위해서 작성한 거라고 합니다
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    // - JPA Criteria는 JPA 표준 스펙이지만 실무에서 사용하기에 너무 복잡하다
    // - 결국 다른 대안이 필요 => Query DSL을 사용한다
    // - QueryDSL 소개에서 간단히 언급 후, 위의 쿼리를 QueryDSL로 바꾼다고 합니다
}
