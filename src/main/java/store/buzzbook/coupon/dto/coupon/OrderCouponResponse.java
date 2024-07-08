package store.buzzbook.coupon.dto.coupon;

import java.time.LocalDate;

import lombok.Builder;
import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.entity.Coupon;

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
