package store.buzzbook.coupon.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.coupon.dto.coupon.CreateUserCouponRequest;

/**
 * 사용자 쿠폰 생성 요청을 처리하는 Feign 클라이언트 인터페이스입니다.
 * <p>
 * 이 인터페이스는 사용자 계정 서비스와의 통신을 위해 사용됩니다.
 * </p>
 */
@FeignClient(name = "UserCouponAdapter", url = "http://${api.gateway.host}:" + "${api.gateway.port}/api/account")
public interface UserAdapter {

	/**
	 * 사용자 쿠폰을 생성합니다.
	 *
	 * @param request 사용자 쿠폰 생성 요청 객체
	 * @return 응답 엔티티
	 */
	@PostMapping("/coupons/batch")
	ResponseEntity<Void> createUserCoupon(@RequestBody CreateUserCouponRequest request);
}
