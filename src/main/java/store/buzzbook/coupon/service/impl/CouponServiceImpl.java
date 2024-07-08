package store.buzzbook.coupon.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.common.exception.CouponNotFoundException;
import store.buzzbook.coupon.common.utils.CodeCreator;
import store.buzzbook.coupon.dto.coupon.CouponLogRequest;
import store.buzzbook.coupon.dto.coupon.CouponResponse;
import store.buzzbook.coupon.dto.coupon.CreateCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateCouponResponse;
import store.buzzbook.coupon.dto.coupon.OrderCouponResponse;
import store.buzzbook.coupon.dto.coupon.UpdateCouponRequest;
import store.buzzbook.coupon.entity.Coupon;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.repository.CouponRepository;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;
import store.buzzbook.coupon.service.CouponPolicyService;
import store.buzzbook.coupon.service.CouponService;

/**
 * 쿠폰 서비스 구현 클래스입니다.
 * <p>
 * 쿠폰의 생성, 조회, 수정과 관련된 비즈니스 로직을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private final CouponRepository couponRepository;
	private final CouponPolicyService couponPolicyService;
	private final CouponPolicyRepository couponPolicyRepository;

	/**
	 * 쿠폰 ID로 쿠폰을 조회합니다.
	 *
	 * @param id 쿠폰 ID
	 * @return 조회된 쿠폰 응답 객체
	 * @throws CouponNotFoundException 쿠폰을 찾을 수 없는 경우
	 */
	@Override
	public CouponResponse getCoupon(long id) {
		validateId(id);

		Coupon coupon = couponRepository.findById(id).orElseThrow(CouponNotFoundException::new);

		return CouponResponse.from(coupon);
	}

	/**
	 * 주어진 요청 목록에 따라 쿠폰 상태를 기준으로 모든 쿠폰을 조회합니다.
	 *
	 * @param request 쿠폰 상태 조회 요청 객체 리스트
	 * @return 조회된 쿠폰 응답 객체 리스트
	 * @throws CouponNotFoundException 요청 조건에 맞는 쿠폰을 찾을 수 없는 경우
	 */
	@Override
	public List<CouponResponse> getAllCouponsByStatus(List<CouponLogRequest> request, String couponStatusName) {
		List<CouponResponse> responses = new ArrayList<>();

		if (couponStatusName.equals("all")) {
			for (CouponLogRequest couponLogRequest : request) {
				Coupon coupon = couponRepository.findByCouponCodeAndCouponPolicyId(
						couponLogRequest.couponCode(),
						couponLogRequest.couponPolicyId())
					.orElseThrow(CouponNotFoundException::new);
				responses.add(CouponResponse.from(coupon));
			}
		} else {
			for (CouponLogRequest couponLogRequest : request) {
				Coupon coupon = couponRepository.findByCouponCodeAndCouponPolicyIdAndStatus(
						couponLogRequest.couponCode(),
						couponLogRequest.couponPolicyId(),
						CouponStatus.fromString(couponStatusName))
					.orElseThrow(CouponNotFoundException::new);
				responses.add(CouponResponse.from(coupon));
			}
		}

		return responses;
	}

	@Override
	public List<OrderCouponResponse> getAvailableCoupons(List<CouponLogRequest> request) {
		List<OrderCouponResponse> responses = new ArrayList<>();

		for (CouponLogRequest couponLogRequest : request) {
			responses.add(couponPolicyRepository.findCouponsWithTargetId(couponLogRequest.couponCode()));
		}

		return responses;
	}

	/**
	 * 새로운 쿠폰을 생성합니다.
	 *
	 * @param request 쿠폰 생성 요청 객체
	 * @return 생성된 쿠폰 응답 객체
	 * @throws IllegalArgumentException 요청 객체가 null 인 경우
	 */
	@Override
	public CreateCouponResponse createCoupon(CreateCouponRequest request) {
		if (Objects.isNull(request)) {
			throw new IllegalArgumentException("쿠폰 로그 생성 요청을 찾을 수 없습니다.");
		}

		String couponCode = CodeCreator.createCode();
		while (Boolean.TRUE.equals(couponRepository.existsByCouponCode(couponCode))) {
			couponCode = CodeCreator.createCode();
		}

		CouponPolicy couponPolicy = couponPolicyService.getCouponPolicyById(request.couponPolicyId());
		LocalDate now = LocalDate.now();

		Coupon coupon = Coupon.builder()
			.couponCode(couponCode)
			.createDate(now)
			.expireDate(now.plusDays(couponPolicy.getPeriod()))
			.status(CouponStatus.AVAILABLE)
			.couponPolicy(couponPolicy)
			.build();

		return CreateCouponResponse.from(couponRepository.save(coupon));
	}

	/**
	 * 쿠폰을 업데이트합니다.
	 *
	 * @param request 쿠폰 업데이트 요청 객체
	 * @return 업데이트된 쿠폰 응답 객체
	 * @throws CouponNotFoundException 쿠폰을 찾을 수 없는 경우
	 * @throws IllegalArgumentException 요청 객체가 null 인 경우
	 */
	@Transactional
	@Override
	public CouponResponse updateCoupon(UpdateCouponRequest request) {
		if (Objects.isNull(request)) {
			throw new IllegalArgumentException("쿠폰 로그 수정 요청을 찾을 수 없습니다.");
		}

		if (!couponRepository.existsByCouponCode(request.couponCode())) {
			throw new CouponNotFoundException();
		}

		Coupon coupon = couponRepository.findByCouponCode(request.couponCode())
			.orElseThrow(CouponNotFoundException::new);

		coupon.changeStatus(request.status());

		return CouponResponse.from(coupon);
	}

	/**
	 * 주어진 ID가 유효한지 확인합니다.
	 *
	 * @param id 유효성을 검사할 ID
	 * @throws IllegalArgumentException ID가 유효하지 않은 경우
	 */
	private void validateId(long id) {
		if (id <= 0) {
			throw new IllegalArgumentException("잘못된 파라미터 요청입니다.");
		}
	}
}
