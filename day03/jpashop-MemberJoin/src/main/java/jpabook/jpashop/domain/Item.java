package jpabook.jpashop.domain;

import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직 추가
    // 데이터를 가지고 있는 쪽이 비즈니스 로직을 가지고 있는 편이 응집력이 있고, 관리하기에도 좋다
    // 1. 재고를 추가하는 로직
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // 2. 주문을 하면, 개수에 따라서 재고가 감소하는 로직
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        // 남은 재고가 0보다 작으면 안되기 때문에 검증 로직 추가
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        // 위의 로직에 걸리지 않으면 현재 재고량을 남은 재고로 수정
        this.stockQuantity = restStock;
    }
}

