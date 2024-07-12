package store.buzzbook.coupon.dto.couponpolicy;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/**
 * 쿠폰 정책 업데이트 요청 데이터를 담는 클래스입니다.
 * <p>
 * 이 클래스는 쿠폰 정책의 종료일을 포함합니다.
 * </p>
 *
 * @param endDate 쿠폰 정책의 종료일
 */
public record UpdateCouponPolicyRequest(

	@NotNull(message = "endDate 는 null 일 수 없습니다.")
	LocalDate endDate
) {
}
