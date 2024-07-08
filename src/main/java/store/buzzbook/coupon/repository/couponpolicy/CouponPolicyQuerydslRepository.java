package store.buzzbook.coupon.repository.couponpolicy;

import java.util.List;

import org.springframework.data.domain.Page;

import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.dto.coupon.OrderCouponResponse;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyConditionRequest;
import store.buzzbook.coupon.entity.CouponPolicy;

/**
 * 쿠폰 정책에 대한 QueryDSL 기반의 커스텀 레포지토리 인터페이스입니다.
 * <p>
 * 이 인터페이스는 다양한 조건에 따라 쿠폰 정책을 검색하는 메서드를 정의합니다.
 * </p>
 */
public interface CouponPolicyQuerydslRepository {

	/**
	 * 책 ID에 따라 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param bookId 책 ID
	 * @return 책 ID에 해당하는 쿠폰 정책 리스트
	 */
	List<CouponPolicy> findAllByBookId(int bookId);

	/**
	 * 쿠폰 범위에 따라 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param couponScope 쿠폰 범위
	 * @return 쿠폰 범위에 해당하는 쿠폰 정책 리스트
	 */
	List<CouponPolicy> findAllByCouponScope(CouponScope couponScope);

	/**
	 * 조건에 따라 모든 쿠폰 정책을 페이징 처리하여 조회합니다.
	 *
	 * @param condition 쿠폰 정책 조회 조건
	 * @return 페이징 처리된 쿠폰 정책 리스트
	 */
	Page<CouponPolicy> findAllByCondition(CouponPolicyConditionRequest condition);

	OrderCouponResponse findCouponsWithTargetId(String couponCode);

}
