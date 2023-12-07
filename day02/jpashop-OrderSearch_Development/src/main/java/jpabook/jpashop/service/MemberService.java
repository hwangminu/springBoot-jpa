package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
// 트랜잭션, 영속성 컨텍스트
// readOnly=true : 데이터의 변경이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 플러시 하지 않으므로 약산의 성능 향상(읽기 전용에는 다 적용)
// 데이터베이스 드라이버가 지원하면 DB에서 성능 향상
//@AllArgsConstructor   // 모든 필드에 대한 생성자를 만들어준다
@RequiredArgsConstructor    // final이 붙어있는 필드만 생성자를 만들어준다
public class MemberService {
    
    // 스프링 필드 주입 대신에 생성자 주입을 사용하자
    // 스프링 필드 주입
//    @Autowired  // 생성자 Injection 많이 사용, 생성자가 하나면 생략 가능
//    MemberRepository memberRepository;

    // setter 주입
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 생성자 주입
    private final MemberRepository memberRepository;
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
    // 생성자 주입 방식을 권장
    // 변경 불가능한 안전한 객체 생성 가능
    // 생성자가 하나면, @Autowired를 생략할 수 있다
    // final 키워드를 추가하면 컴파일 시점에 memberRepository를 설정하지 않는 오류를 체크할 수 있다(보통 기본 생성자를 추가할 때 발견)
    // 롬복을 사용한다면 @AllArgsConstructor나 @RequiredArgsConstructor를 사용해서 어노테이션으로 대체할 수 있다

    // 회원가입
    @Transactional  // 읽기 전용이 아닌 쓰기로 변경
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // 실무에서는 검증 로직이 있어도 멀티 쓰레드 상황을 고려해서 회원 테이블의 회원명 컬럼에 유니크 제약 조건을 추가하는 것이 안전하다
        // EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
