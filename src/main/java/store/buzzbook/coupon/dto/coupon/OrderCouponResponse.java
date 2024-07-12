package store.buzzbook.coupon.dto.coupon;

import java.time.LocalDate;

import lombok.Builder;
import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.entity.Coupon;

/**
 * 주문 시 사용 가능한 쿠폰 응답 데이터를 담는 클래스입니다.
 * <p>
 * 이 클래스는 쿠폰 코드, 생성일, 만료일, 상태, 쿠폰 정책 응답 데이터, 타겟 ID를 포함합니다.
 * </p>
 *
 * @param code 쿠폰 코드
 * @param createDate 쿠폰 생성일
 * @param expireDate 쿠폰 만료일
 * @param status 쿠폰 상태
 * @param couponPolicyResponse 쿠폰 정책 응답 데이터
 * @param targetId 타겟 ID
 */
@Builder
public record OrderCouponResponse(
	String code,
	LocalDate createDate,
	LocalDate expireDate,
	CouponStatus status,
	CouponPolicyResponse couponPolicyResponse,
	Integer targetId
) {
	public static OrderCouponResponse from(Coupon coupon) {
		return new OrderCouponResponse(
			coupon.getCouponCode(),
			coupon.getCreateDate(),
			coupon.getExpireDate(),
			coupon.getStatus(),
			CouponPolicyResponse.from(coupon.getCouponPolicy()),
			null
		);
	}
}
