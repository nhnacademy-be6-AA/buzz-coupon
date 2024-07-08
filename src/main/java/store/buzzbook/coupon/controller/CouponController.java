package store.buzzbook.coupon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import store.buzzbook.coupon.dto.coupon.CouponLogRequest;
import store.buzzbook.coupon.dto.coupon.CouponResponse;
import store.buzzbook.coupon.dto.coupon.CreateCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateCouponResponse;
import store.buzzbook.coupon.dto.coupon.OrderCouponResponse;
import store.buzzbook.coupon.dto.coupon.UpdateCouponRequest;
import store.buzzbook.coupon.service.CouponService;

/**
 * 쿠폰 관련 API 를 처리하는 컨트롤러 클래스입니다.
 * <p>
 * 쿠폰 조회, 발급, 수정 기능을 제공합니다.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
@Tag(name = "쿠폰 내역 API", description = "쿠폰 내역 API 전체 설명서")
public class CouponController {

	private final CouponService couponService;

	/**
	 * 쿠폰 정보를 조회합니다.
	 *
	 * @param couponId 조회할 쿠폰의 ID
	 * @return 조회된 쿠폰 정보를 담은 ResponseEntity 객체
	 */
	@GetMapping("/{couponId}")
	@Transactional(readOnly = true)
	@Operation(summary = "쿠폰 조회", description = "쿠폰 정보를 조회합니다.")
	public ResponseEntity<CouponResponse> getCoupon(@PathVariable long couponId) {
		return ResponseEntity.ok(couponService.getCoupon(couponId));
	}

	@PostMapping("/order")
	@Operation(summary = "회원 쿠폰 조회", description = "회원이 가진 사용가능한 쿠폰 정보를 조회합니다.")
	public ResponseEntity<List<OrderCouponResponse>> getUserCoupons(
		@Valid @RequestBody List<CouponLogRequest> request) {
		return ResponseEntity.ok(couponService.getAvailableCoupons(request));
	}

	/**
	 * 회원이 가진 쿠폰 정보를 조회합니다.
	 *
	 * @param request 쿠폰 상태 조회 요청 객체 리스트
	 * @return 조회된 쿠폰 응답 객체 리스트를 포함한 ResponseEntity
	 */
	@PostMapping("/condition")
	@Transactional(readOnly = true)
	@Operation(summary = "회원 쿠폰 조회", description = "회원이 가진 쿠폰 정보를 상태에 따라 조회합니다.")
	public ResponseEntity<List<CouponResponse>> getUserCoupons(@Valid @RequestBody List<CouponLogRequest> request,
		@RequestParam String couponStatusName) {
		return ResponseEntity.ok(couponService.getAllCouponsByStatus(request, couponStatusName));
	}

	/**
	 * 쿠폰을 발급합니다.
	 *
	 * @param request 쿠폰 발급 요청 정보를 담은 객체
	 * @return 발급된 쿠폰 정보를 담은 ResponseEntity 객체
	 */
	@PostMapping
	@Transactional
	@Operation(summary = "쿠폰 발급", description = "쿠폰을 발급합니다.")
	public ResponseEntity<CreateCouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(request));
	}

	/**
	 * 쿠폰 상태를 수정합니다.
	 *
	 * @param request 쿠폰 수정 요청 정보를 담은 객체
	 * @return 수정된 쿠폰 정보를 담은 ResponseEntity 객체
	 */
	@PutMapping
	@Operation(summary = "쿠폰 수정", description = "쿠폰 상태를 수정합니다.")
	public ResponseEntity<CouponResponse> updateCoupon(@Valid @RequestBody UpdateCouponRequest request) {
		return ResponseEntity.ok(couponService.updateCoupon(request));
	}
}
