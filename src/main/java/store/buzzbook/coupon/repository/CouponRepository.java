package store.buzzbook.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.entity.Coupon;

/**
 * 쿠폰에 대한 JPA 레포지토리 인터페이스입니다.
 * <p>
 * 이 인터페이스는 기본적인 CRUD 메서드를 제공합니다.
 * </p>
 */
public interface CouponRepository extends JpaRepository<Coupon, Long> {

	Boolean existsByCouponCode(String couponCode);

	Optional<Coupon> findByCouponCode(String couponCode);

	Optional<Coupon> findByCouponCodeAndCouponPolicyId(String couponCode, int couponPolicyId);

	Optional<Coupon> findByCouponCodeAndCouponPolicyIdAndStatus(String couponCode, int couponPolicyId,
		CouponStatus couponStatus);
}
