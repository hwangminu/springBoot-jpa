package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 주문 회원

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;  // 배송 정보

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>(); 

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 연관관계 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 생성 메서드
    // 여러 연관관계가 묶여있는 복잡한 생성은 생성 메서드가 따로 있는 편이 좋다
    // - 주문 엔티티를 생성할 때 사용
    // - 주문 회원, 배송정보, 주문 상품의 정보를 받아서 실제 주문 엔티티를 생성한다
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    
    // 비즈니스 로직
    // 1. 주문 취소
    // - 주문 취소 시 사용
    // - 주문 상태를 취소로 변경하고, 주문 상품에 주문 취소를 알린다
    // - 이미 배송을 완료한 상품이면 주문을 취소하지 못하도록 예외를 발생시킨다
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {  // 만약 배송 상태가 이미 배송 중이라면
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
        }

        this.setStatus(OrderStatus.CANCEL); // 배송중이 아니라면 현재 주문 상태를 취소로 변경한다
        for (OrderItem orderItem : orderItems) {    // 현재 주문 물품 리스트만큼 반복하면서
            orderItem.cancel(); // 해당 제품들의 재고를 주문 수량 만큼 증가시킨다 => OrderItem에서 생성
        }
    }

    // 조회 로직
    // 1. 전체 주문 가격 조회
    // - 주문 시 사용한 전체 주문 가격을 조회한다
    // - 전체 주문 가격을 알려면 각각의 주문 상품 가격을 알아야 한다
    //  - 연관된 주문 상품들의 가격을 조회해서 더한 값을 반환한다
    //  - 실무에서는 주로 주문에 전체 주문 가격 필드를 두고 역정규화 한다)
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
