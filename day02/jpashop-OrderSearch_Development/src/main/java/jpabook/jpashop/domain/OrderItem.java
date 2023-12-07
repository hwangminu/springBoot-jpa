package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격, 실제 상품의 가격과는 다르게 할인을 받거나 하는 경우도 포함되어 있다
    private int count;

//    protected OrderItem() {}
    // 생성 메서드를 사용하는 경우, 기본 생성자를 막아두는 것이 낫다
    // - 누구는 생성 메서드를, 누구는 객체 생성 후 setter를 사용하여 코드를 작성하면 유지보수가 어려워진다
    // - lombok에서는 @NoArgsConstructor(access = AccessLevel.PROTECTED)를 사용하여 기본 생성자를 protected로 설정할 수 있다

    // 생성 메서드
    // - 주문 상품, 가격, 수량 정보를 사용해서 주문상품 엔티티를 생성한다
    // - item.removeStock(count)를 호출해서 주문한 수량만큼 상품의 재고를 줄인다
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    // 비즈니스 로직
    // 1. 주문 취소
    // - getItem().addStock(count)를 호출해서 취소한 주문 수량만큼 상품의 재고를 증가시킨다
    public void cancel() {
        getItem().addStock(count);  // item의 재고 수량을 주문 수량만큼 증가시킨다
    }
    
    // 2. 주문 가격 조회
    // - 주문 가격에 수량을 곱한 값을 반환한다
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
