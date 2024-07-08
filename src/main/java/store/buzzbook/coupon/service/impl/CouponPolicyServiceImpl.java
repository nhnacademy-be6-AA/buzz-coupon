package store.buzzbook.coupon.service.impl;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.common.exception.CouponPolicyNotFoundException;
import store.buzzbook.coupon.common.utils.DateFormatter;
import store.buzzbook.coupon.dto.coupon.CouponPoliciesResponse;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyConditionRequest;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyRequest;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.UpdateCouponPolicyRequest;
import store.buzzbook.coupon.entity.CategoryCoupon;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.entity.SpecificCoupon;
import store.buzzbook.coupon.repository.CategoryCouponRepository;
import store.buzzbook.coupon.repository.SpecificCouponRepository;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;
import store.buzzbook.coupon.service.CouponPolicyService;
import store.buzzbook.coupon.service.CouponTypeService;

/**
 * 쿠폰 정책 서비스 구현 클래스입니다.
 * <p>
 * 쿠폰 정책의 생성, 조회, 수정, 삭제와 관련된 비즈니스 로직을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CouponPolicyServiceImpl implements CouponPolicyService {

	private final CouponPolicyRepository couponPolicyRepository;
	private final CategoryCouponRepository categoryCouponRepository;
	private final SpecificCouponRepository specificCouponRepository;
	private final CouponTypeService couponTypeService;

	/**
	 * 조건에 따라 페이징 처리된 쿠폰 정책 리스트를 조회합니다.
	 *
	 * @param condition 쿠폰 정책 조회 조건
	 * @return 페이징 처리된 쿠폰 정책 응답 리스트
	 */
	@Override
	public Page<CouponPolicyResponse> getCouponPoliciesByPaging(CouponPolicyConditionRequest condition) {
		Page<CouponPolicy> couponPolicies = couponPolicyRepository.findAllByCondition(condition);
		return couponPolicies.map(CouponPolicyResponse::from);
	}

	/**
	 * 쿠폰 범위에 따라 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param scope 쿠폰 범위 리스트
	 * @return 쿠폰 범위에 따른 쿠폰 정책 응답 리스트
	 */
	@Override
	public CouponPoliciesResponse getCouponPoliciesByScope(List<String> scope) {
		Map<CouponScope, List<CouponPolicyResponse>> policiesMap = new EnumMap<>(CouponScope.class);
		policiesMap.put(CouponScope.GLOBAL, new ArrayList<>());
		policiesMap.put(CouponScope.BOOK, new ArrayList<>());
		policiesMap.put(CouponScope.CATEGORY, new ArrayList<>());

		for (String scopeName : scope) {
			CouponScope couponScope = CouponScope.fromString(scopeName);
			if (couponScope != null) {
				List<CouponPolicyResponse> filteredPolicies = couponPolicyRepository
					.findAllByCouponScope(couponScope)
					.stream()
					.map(CouponPolicyResponse::from)
					.toList();

				if (couponScope == CouponScope.GLOBAL) {
					filteredPolicies = filteredPolicies.stream()
						.filter(policy -> !policy.name().contains("생일"))
						.toList();
				}

				policiesMap.put(couponScope, filteredPolicies);
			}
		}

		return CouponPoliciesResponse.builder()
			.globalCouponPolicies(policiesMap.get(CouponScope.GLOBAL))
			.specificCouponPolicies(policiesMap.get(CouponScope.BOOK))
			.categoryCouponPolicies(policiesMap.get(CouponScope.CATEGORY))
			.build();
	}

	/**
	 * 특정 책에 대한 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param bookId 책 ID
	 * @return 특정 책에 대한 쿠폰 정책 응답 리스트
	 */
	@Override
	public List<CouponPolicyResponse> getSpecificCoupons(int bookId) {
		validateId(bookId);

		return couponPolicyRepository.findAllByBookId(bookId).stream()
			.map(CouponPolicyResponse::from)
			.toList();
	}

	/**
	 * 쿠폰 정책 ID로 쿠폰 정책을 조회합니다.
	 *
	 * @param id 쿠폰 정책 ID
	 * @return 쿠폰 정책 엔티티
	 * @throws CouponPolicyNotFoundException 쿠폰 정책을 찾을 수 없는 경우
	 */
	@Override
	public CouponPolicy getCouponPolicyById(int id) {
		validateId(id);
		return couponPolicyRepository.findById(id).orElseThrow(CouponPolicyNotFoundException::new);
	}

	/**
	 * 새로운 쿠폰 정책을 생성합니다.
	 *
	 * @param request 쿠폰 정책 생성 요청 객체
	 * @return 생성된 쿠폰 정책 응답 객체
	 */
	@Transactional
	@Override
	public CreateCouponPolicyResponse createCouponPolicy(CreateCouponPolicyRequest request) {
		if (Objects.isNull(request)) {
			throw new IllegalArgumentException("쿠폰 생성 요청을 찾을 수 없습니다.");
		}

		CouponType couponType = couponTypeService.getCouponType(request.couponType());

		CouponPolicy couponPolicy = CouponPolicy.builder()
			.name(request.name())
			.discountType(DiscountType.fromString(request.discountType()))
			.discountRate(request.discountRate())
			.discountAmount(request.discountAmount())
			.standardPrice(request.standardPrice())
			.maxDiscountAmount(request.maxDiscountAmount())
			.period(request.period())
			.startDate(DateFormatter.toLocalDate(request.startDate()))
			.endDate(DateFormatter.toLocalDate(request.endDate()))
			.couponType(couponType)
			.deleted(false)
			.build();

		CouponPolicy savedPolicy = couponPolicyRepository.save(couponPolicy);

		if (couponType.getName().equals(CouponScope.BOOK)) {
			SpecificCoupon specificCoupon = SpecificCoupon.builder()
				.couponPolicy(savedPolicy)
				.bookId(request.targetId())
				.build();

			specificCouponRepository.save(specificCoupon);
		} else if (couponType.getName().equals(CouponScope.CATEGORY)) {
			CategoryCoupon categoryCoupon = CategoryCoupon.builder()
				.couponPolicy(savedPolicy)
				.categoryId(request.targetId())
				.build();

			categoryCouponRepository.save(categoryCoupon);
		}

		return CreateCouponPolicyResponse.from(savedPolicy);
	}

	/**
	 * 쿠폰 정책을 업데이트합니다.
	 *
	 * @param id 쿠폰 정책 ID
	 * @param request 쿠폰 정책 업데이트 요청 객체
	 * @return 업데이트된 쿠폰 정책 응답 객체
	 */
	@Override
	public CouponPolicyResponse updateCouponPolicy(int id, UpdateCouponPolicyRequest request) {
		validateId(id);

		if (Objects.isNull(request)) {
			throw new IllegalArgumentException("쿠폰 생성 요청을 찾을 수 없습니다.");
		}

		CouponPolicy couponPolicy = getCouponPolicyById(id);
		couponPolicy.changeEndDate(request.endDate());

		return CouponPolicyResponse.from(couponPolicy);
	}

	/**
	 * 쿠폰 정책을 삭제 상태로 변경합니다.
	 *
	 * @param id 쿠폰 정책 ID
	 */
	@Override
	public void deleteCouponPolicy(int id) {
		validateId(id);

		if (!couponPolicyRepository.existsById(id)) {
			throw new CouponPolicyNotFoundException();
		}

		CouponPolicy couponPolicy = getCouponPolicyById(id);
		couponPolicy.delete();
	}

	/**
	 * 주어진 ID가 유효한지 확인합니다.
	 *
	 * @param id 유효성을 검사할 ID
	 * @throws IllegalArgumentException ID가 유효하지 않은 경우
	 */
	private void validateId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("잘못된 파라미터 값입니다.");
		}
	}
}
