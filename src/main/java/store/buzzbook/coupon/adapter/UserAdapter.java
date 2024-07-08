package store.buzzbook.coupon.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.coupon.dto.coupon.CreateUserCouponRequest;

@FeignClient(name = "UserCouponAdapter", url = "http://${api.gateway.host}:" + "${api.gateway.port}/api/account")
public interface UserAdapter {

	@PostMapping("/coupons/batch")
	ResponseEntity<Void> createUserCoupon(@RequestBody CreateUserCouponRequest request);
}
