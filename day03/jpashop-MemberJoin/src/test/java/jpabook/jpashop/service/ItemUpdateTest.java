package jpabook.jpashop.service;

import jpabook.jpashop.domain.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateItem() throws Exception{
        Book book = em.find(Book.class, 1L);

        // tx
        book.setName("asdasd");

       //then
    }
}
