package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    // 주문에는 회원과 상품이 필요하기 때문에 각각의 리포지토리가 필요하다
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     * - 주문하는 회원 식별자, 상품 식별자, 주문 수량 정보를 받아서 실제 주문 엔티티를 생성한 후 저장한다
     * - 예제를 단순화하기 위해 한 번에 하나의 상품만 주문할 수 있다
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());   // 지금은 배송 주소를 회원 주소와 통일하지만, 실제로는 배송 주소를 입력받아야 한다
        delivery.setStatus(DeliveryStatus.READY);

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);    // domain에서 설정한 CASCADE 옵션 때문에 ORDER만 저장해도 연관된 모든 엔티티들이 persist된다
        return order.getId();
    }

    // 취소
    // - 주문 식별자를 받아서 주문 엔티티를 조회한 후, 주문 엔티티에 주문 취소를 요청한다
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }

    // 검색
    // - OrderSearch라는 검색 조건을 가진 객체로 주문 엔티티를 검색한다
    // - 자세한 내용은 다음에 나오는 주문 검색 기능에서 알아본다
   public List<Order> findOrders(OrderSearch orderSearch) {
       return orderRepository.findAll(orderSearch);
    }
}

// 주문 서비스의 주문과 주문 취소 메서드를 보면 비즈니스 로직 대부분이 엔티티에 있다
// 서비스 계층은 단순히 엔티티에 필요한 요청을 위임하는 역할을 한다
// 이처럼 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용하는 것을 도메인 모델 패턴 이라 한다
// 반대로 엔티티에는 비즈니스 로직이 거의 없고, 서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것을 트랜잭션 스크립트 패턴 이라 한다
