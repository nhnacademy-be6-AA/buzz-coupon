package store.buzzbook.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.coupon.entity.SpecificCoupon;

/**
 * 특정 쿠폰에 대한 JPA 레포지토리 인터페이스입니다.
 * <p>
 * 이 인터페이스는 기본적인 CRUD 메서드를 제공합니다.
 * </p>
 */
public interface SpecificCouponRepository extends JpaRepository<SpecificCoupon, Integer> {
}
