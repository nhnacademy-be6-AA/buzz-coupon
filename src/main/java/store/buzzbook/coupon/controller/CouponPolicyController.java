package store.buzzbook.coupon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import store.buzzbook.coupon.dto.coupon.CouponPoliciesResponse;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyConditionRequest;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyRequest;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.UpdateCouponPolicyRequest;
import store.buzzbook.coupon.dto.coupontype.CouponTypeResponse;
import store.buzzbook.coupon.service.CouponPolicyService;
import store.buzzbook.coupon.service.CouponTypeService;

/**
 * 쿠폰 정책 관련 API 를 처리하는 컨트롤러 클래스입니다.
 * <p>
 * 쿠폰 정책의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/policies")
@Tag(name = "쿠폰 정책 API", description = "쿠폰 정책 API 전체 설명서")
public class CouponPolicyController {

	private final CouponPolicyService couponPolicyService;
	private final CouponTypeService couponTypeService;

	/**
	 * 조건과 페이징 처리된 모든 쿠폰 정책 리스트를 조회합니다.
	 *
	 * @param condition 쿠폰 정책 조회 조건
	 * @return 조회된 쿠폰 정책 리스트를 담은 페이지 객체
	 */
	@PostMapping("/condition")
	@Transactional(readOnly = true)
	@Operation(summary = "쿠폰 정책 리스트 조회", description = "조건과 페이징 처리된 모든 쿠폰 정책 리스트를 조회합니다.")
	public ResponseEntity<Page<CouponPolicyResponse>> getCouponPoliciesByPaging(
		@Valid @RequestBody CouponPolicyConditionRequest condition, Pageable pageable) {
		return ResponseEntity.ok(couponPolicyService.getCouponPoliciesByPaging(pageable, condition));
	}

	/**
	 * 특정 범위의 다운로드 가능한 쿠폰 정책 리스트를 조회합니다.
	 *
	 * @param scope 쿠폰 정책 범위 리스트
	 * @return 조회된 쿠폰 정책 리스트를 담은 응답 객체
	 */
	@GetMapping
	@Transactional(readOnly = true)
	@Operation(summary = "특정 범위 쿠폰 정책 리스트 조회", description = "특정 범위의 다운로드 가능한 쿠폰 정책 리스트를 조회합니다.")
	public ResponseEntity<CouponPoliciesResponse> getCouponPoliciesByScope(@RequestParam List<String> scope) {
		return ResponseEntity.ok(couponPolicyService.getCouponPoliciesByScope(scope));
	}

	/**
	 * 쿠폰 정책을 생성합니다.
	 *
	 * @param request 쿠폰 정책 생성 요청 정보를 담은 객체
	 * @return 생성된 쿠폰 정책 정보를 담은 응답 객체
	 */
	@PostMapping
	@Transactional
	@Operation(summary = "쿠폰 정책 생성", description = "쿠폰 정책을 생성합니다.")
	public ResponseEntity<CreateCouponPolicyResponse> createCouponPolicy(
		@Valid @RequestBody CreateCouponPolicyRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(couponPolicyService.createCouponPolicy(request));
	}

	/**
	 * 쿠폰 정책의 다운로드가 끝나는 날을 수정합니다.
	 *
	 * @param couponPolicyId 수정할 쿠폰 정책의 ID
	 * @param request 쿠폰 정책 수정 요청 정보를 담은 객체
	 * @return 수정된 쿠폰 정책 정보를 담은 응답 객체
	 */
	@PutMapping("/{couponPolicyId}")
	@Transactional
	@Operation(summary = "쿠폰 정책 수정", description = "쿠폰 정책의 다운로드가 끝나는 날을 수정합니다.")
	public ResponseEntity<CouponPolicyResponse> updateCouponPolicy(@PathVariable int couponPolicyId,
		@Valid @RequestBody UpdateCouponPolicyRequest request) {
		return ResponseEntity.ok(couponPolicyService.updateCouponPolicy(couponPolicyId, request));
	}

	/**
	 * 쿠폰 정책을 삭제합니다.
	 *
	 * @param couponPolicyId 삭제할 쿠폰 정책의 ID
	 * @return 응답 본문이 없는 ResponseEntity 객체
	 */
	@DeleteMapping("/{couponPolicyId}")
	@Transactional
	@Operation(summary = "쿠폰 정책 삭제", description = "쿠폰 정책을 삭제합니다.")
	public ResponseEntity<Void> deleteCouponPolicy(@PathVariable int couponPolicyId) {
		couponPolicyService.deleteCouponPolicy(couponPolicyId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 쿠폰 정책 타입을 조회합니다.
	 *
	 * @return 조회된 쿠폰 타입 리스트를 담은 응답 객체
	 */
	@GetMapping("/types")
	@Transactional(readOnly = true)
	@Operation(summary = "쿠폰 타입 조회", description = "쿠폰 정책 타입을 조회 합니다.")
	public ResponseEntity<List<CouponTypeResponse>> getCouponTypes() {
		return ResponseEntity.ok(couponTypeService.getAllCouponTypes());
	}

	/**
	 * 특정 도서 쿠폰의 리스트를 조회합니다.
	 *
	 * @param bookId 조회할 도서의 ID
	 * @return 조회된 쿠폰 정책 리스트를 담은 응답 객체
	 */
	@GetMapping("/specifics/{bookId}")
	@Transactional(readOnly = true)
	@Operation(summary = "특정 도서 쿠폰 리스트 조회", description = "특정 도서 쿠폰의 리스트를 조회 합니다.")
	public ResponseEntity<List<CouponPolicyResponse>> getSpecificCouponPolicies(@PathVariable int bookId) {
		return ResponseEntity.ok(couponPolicyService.getSpecificCoupons(bookId));
	}
}
