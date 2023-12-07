package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item);   // item은 db에 저장하기 전까지 id값이 없다 => 완전히 새로 생성한 객체 => 신규로 등록한다
        } else {
            em.merge(item); // id값이 있다는 것은 db에 등록된 것을 가져온 것이다 => 삽입이 아닌 수정을 해야한다(merge에 관해서는 나중에 다룬다고 합니다)
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
