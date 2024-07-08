package store.buzzbook.coupon.service;

import java.util.List;

import store.buzzbook.coupon.dto.coupon.CouponLogRequest;
import store.buzzbook.coupon.dto.coupon.CouponResponse;
import store.buzzbook.coupon.dto.coupon.CreateCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateCouponResponse;
import store.buzzbook.coupon.dto.coupon.OrderCouponResponse;
import store.buzzbook.coupon.dto.coupon.UpdateCouponRequest;

/**
 * 쿠폰 서비스 인터페이스입니다.
 * <p>
 * 쿠폰의 생성, 조회, 수정과 관련된 비즈니스 로직을 정의합니다.
 * </p>
 */
public interface CouponService {

	/**
	 * 쿠폰 ID로 쿠폰을 조회합니다.
	 *
	 * @param id 쿠폰 ID
	 * @return 조회된 쿠폰 응답 객체
	 */
	CouponResponse getCoupon(long id);

	/**
	 * 주어진 요청 목록에 따라 쿠폰 상태를 기준으로 모든 쿠폰을 조회합니다.
	 *
	 * @param request 쿠폰 상태 조회 요청 객체 리스트
	 * @return 조회된 쿠폰 응답 객체 리스트
	 */
	List<CouponResponse> getAllCouponsByStatus(List<CouponLogRequest> request, String couponStatusName);

	List<OrderCouponResponse> getAvailableCoupons(List<CouponLogRequest> request);

	/**
	 * 새로운 쿠폰을 생성합니다.
	 *
	 * @param request 쿠폰 생성 요청 객체
	 * @return 생성된 쿠폰 응답 객체
	 */
	CreateCouponResponse createCoupon(CreateCouponRequest request);

	/**
	 * 쿠폰을 업데이트합니다.
	 *
	 * @param id 쿠폰 ID
	 * @param request 쿠폰 업데이트 요청 객체
	 * @return 업데이트된 쿠폰 응답 객체
	 */
	CouponResponse updateCoupon(UpdateCouponRequest request);
}
