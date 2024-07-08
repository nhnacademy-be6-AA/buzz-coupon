package store.buzzbook.coupon.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import store.buzzbook.coupon.common.constant.CouponStatus;

/**
 * 쿠폰 상태 업데이트 요청 데이터를 담는 클래스입니다.
 * <p>
 * 이 클래스는 업데이트할 쿠폰의 상태를 포함합니다.
 * </p>
 *
 * @param status 업데이트할 쿠폰의 상태
 */
public record UpdateCouponRequest(

	@NotBlank(message = "쿠폰 코드는 null 일 수 없습니다.")
	String couponCode,

	@NotBlank(message = "status 는 null 일 수 없습니다.")
	CouponStatus status
) {
}
