package store.buzzbook.coupon.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.coupon.dto.coupon.CouponPoliciesResponse;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyConditionRequest;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyRequest;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.UpdateCouponPolicyRequest;
import store.buzzbook.coupon.entity.CouponPolicy;

/**
 * 쿠폰 정책 서비스 인터페이스입니다.
 * <p>
 * 쿠폰 정책의 생성, 조회, 수정, 삭제와 관련된 비즈니스 로직을 정의합니다.
 * </p>
 */
public interface CouponPolicyService {

	/**
	 * 조건에 따라 페이징 처리된 쿠폰 정책 리스트를 조회합니다.
	 *
	 * @param condition 쿠폰 정책 조회 조건
	 * @return 페이징 처리된 쿠폰 정책 응답 리스트
	 */
	Page<CouponPolicyResponse> getCouponPoliciesByPaging(Pageable pageable, CouponPolicyConditionRequest condition);

	/**
	 * 쿠폰 범위에 따라 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param scope 쿠폰 범위 리스트
	 * @return 쿠폰 범위에 따른 쿠폰 정책 응답 리스트
	 */
	CouponPoliciesResponse getCouponPoliciesByScope(List<String> scope);

	/**
	 * 특정 책에 대한 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param bookId 책 ID
	 * @return 특정 책에 대한 쿠폰 정책 응답 리스트
	 */
	List<CouponPolicyResponse> getSpecificCoupons(int bookId);

	/**
	 * 쿠폰 정책 ID로 쿠폰 정책을 조회합니다.
	 *
	 * @param id 쿠폰 정책 ID
	 * @return 쿠폰 정책 엔티티
	 */
	CouponPolicy getCouponPolicyById(int id);

	/**
	 * 새로운 쿠폰 정책을 생성합니다.
	 *
	 * @param request 쿠폰 정책 생성 요청 객체
	 * @return 생성된 쿠폰 정책 응답 객체
	 */
	CreateCouponPolicyResponse createCouponPolicy(CreateCouponPolicyRequest request);

	/**
	 * 쿠폰 정책을 업데이트합니다.
	 *
	 * @param id 쿠폰 정책 ID
	 * @param request 쿠폰 정책 업데이트 요청 객체
	 * @return 업데이트된 쿠폰 정책 응답 객체
	 */
	CouponPolicyResponse updateCouponPolicy(int id, UpdateCouponPolicyRequest request);

	/**
	 * 쿠폰 정책을 삭제 상태로 변경합니다.
	 *
	 * @param id 쿠폰 정책 ID
	 */
	void deleteCouponPolicy(int id);
}
