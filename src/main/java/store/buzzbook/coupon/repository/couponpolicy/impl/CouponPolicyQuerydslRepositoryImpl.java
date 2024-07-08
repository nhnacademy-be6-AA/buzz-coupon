package store.buzzbook.coupon.repository.couponpolicy.impl;

import static store.buzzbook.coupon.entity.QCouponPolicy.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.dto.coupon.OrderCouponResponse;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyConditionRequest;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.entity.Coupon;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.QCategoryCoupon;
import store.buzzbook.coupon.entity.QCoupon;
import store.buzzbook.coupon.entity.QCouponPolicy;
import store.buzzbook.coupon.entity.QSpecificCoupon;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyQuerydslRepository;

/**
 * 쿠폰 정책에 대한 QueryDSL 기반의 커스텀 레포지토리 구현 클래스입니다.
 * <p>
 * 이 클래스는 쿠폰 정책의 조회 조건을 처리하고, 다양한 조건에 따라 쿠폰 정책을 검색합니다.
 * </p>
 */
@Slf4j
@Repository
public class CouponPolicyQuerydslRepositoryImpl extends QuerydslRepositorySupport
	implements CouponPolicyQuerydslRepository {

	/**
	 * 기본 생성자입니다. 쿠폰 정책 엔티티 클래스를 설정합니다.
	 */
	public CouponPolicyQuerydslRepositoryImpl() {
		super(CouponPolicy.class);
	}

	/**
	 * 책 ID에 따라 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param bookId 책 ID
	 * @return 책 ID에 해당하는 쿠폰 정책 리스트
	 */
	@Override
	public List<CouponPolicy> findAllByBookId(int bookId) {
		QCouponPolicy couponPolicy = QCouponPolicy.couponPolicy;
		QSpecificCoupon specificCoupon = QSpecificCoupon.specificCoupon;

		return from(specificCoupon)
			.join(specificCoupon.couponPolicy, couponPolicy)
			.where(specificCoupon.bookId.eq(bookId),
				couponPolicy.deleted.eq(false),
				couponPolicy.startDate.before(LocalDate.now()),
				couponPolicy.endDate.after(LocalDate.now()))
			.select(couponPolicy)
			.fetch();
	}

	/**
	 * 쿠폰 범위에 따라 모든 쿠폰 정책을 조회합니다.
	 *
	 * @param couponScope 쿠폰 범위
	 * @return 쿠폰 범위에 해당하는 쿠폰 정책 리스트
	 */
	@Override
	public List<CouponPolicy> findAllByCouponScope(CouponScope couponScope) {
		QCouponPolicy couponPolicy = QCouponPolicy.couponPolicy;

		return from(couponPolicy)
			.where(
				couponPolicy.couponType.name.eq(couponScope),
				couponPolicy.deleted.eq(false),
				couponPolicy.startDate.before(LocalDate.now()),
				couponPolicy.endDate.after(LocalDate.now()))
			.select(couponPolicy)
			.fetch();
	}

	/**
	 * 조건에 따라 모든 쿠폰 정책을 페이징 처리하여 조회합니다.
	 *
	 * @param condition 쿠폰 정책 조회 조건
	 * @return 페이징 처리된 쿠폰 정책 리스트
	 */
	@Override
	public Page<CouponPolicy> findAllByCondition(CouponPolicyConditionRequest condition) {
		QCouponPolicy couponPolicy = QCouponPolicy.couponPolicy;

		List<CouponPolicy> couponPolicies = from(couponPolicy)
			.where(
				discountTypeEq(condition.discountTypeName()),
				isDeletedEq(condition.isDeleted()),
				couponTypeEq(condition.couponTypeName()))
			.offset(condition.pageable().getOffset())
			.limit(condition.pageable().getPageSize())
			.select(couponPolicy)
			.fetch();

		long total = from(couponPolicy)
			.where(
				discountTypeEq(condition.discountTypeName()),
				isDeletedEq(condition.isDeleted()),
				couponTypeEq(condition.couponTypeName()))
			.fetch().size();

		return new PageImpl<>(couponPolicies, condition.pageable(), total);
	}

	@Override
	public OrderCouponResponse findCouponsWithTargetId(String couponCode) {
		QCouponPolicy couponPolicy = QCouponPolicy.couponPolicy;
		QCoupon coupon = QCoupon.coupon;
		QSpecificCoupon specificCoupon = QSpecificCoupon.specificCoupon;
		QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;

		Coupon fetchedCoupon = from(coupon)
			.leftJoin(coupon.couponPolicy, couponPolicy)
			.leftJoin(specificCoupon).on(couponPolicy.id.eq(specificCoupon.couponPolicy.id))
			.leftJoin(categoryCoupon).on(couponPolicy.id.eq(categoryCoupon.couponPolicy.id))
			.where(coupon.couponCode.eq(couponCode), coupon.status.eq(CouponStatus.AVAILABLE))
			.fetchOne();

		if (fetchedCoupon == null) {
			return null;
		}

		return OrderCouponResponse.builder()
			.code(fetchedCoupon.getCouponCode())
			.createDate(fetchedCoupon.getCreateDate())
			.expireDate(fetchedCoupon.getExpireDate())
			.status(fetchedCoupon.getStatus())
			.couponPolicyResponse(CouponPolicyResponse.from(fetchedCoupon.getCouponPolicy()))
			.targetId(getTargetId(fetchedCoupon.getCouponPolicy()))
			.build();
	}

	private Integer getTargetId(CouponPolicy policy) {
		if (policy.getCouponType().getName() == CouponScope.GLOBAL) {
			return 0;
		} else if (policy.getCouponType().getName() == CouponScope.BOOK) {
			return from(QSpecificCoupon.specificCoupon)
				.where(QSpecificCoupon.specificCoupon.couponPolicy.id.eq(policy.getId()))
				.select(QSpecificCoupon.specificCoupon.bookId)
				.fetchOne();
		} else if (policy.getCouponType().getName() == CouponScope.CATEGORY) {
			return from(QCategoryCoupon.categoryCoupon)
				.where(QCategoryCoupon.categoryCoupon.couponPolicy.id.eq(policy.getId()))
				.select(QCategoryCoupon.categoryCoupon.categoryId)
				.fetchOne();
		}
		return null;
	}

	/**
	 * 할인 타입에 따른 조건을 생성합니다.
	 *
	 * @param discountType 할인 타입
	 * @return 할인 타입에 따른 조건
	 */
	private BooleanExpression discountTypeEq(String discountType) {
		return !Objects.equals(discountType, "ALL")
			? couponPolicy.discountType.eq(DiscountType.valueOf(discountType)) : null;
	}

	/**
	 * 삭제 여부에 따른 조건을 생성합니다.
	 *
	 * @param isDeleted 삭제 여부
	 * @return 삭제 여부에 따른 조건
	 */
	private BooleanExpression isDeletedEq(String isDeleted) {
		return !Objects.equals(isDeleted, "ALL")
			? couponPolicy.deleted.eq(Boolean.valueOf(isDeleted)) : null;
	}

	/**
	 * 쿠폰 타입에 따른 조건을 생성합니다.
	 *
	 * @param couponType 쿠폰 타입
	 * @return 쿠폰 타입에 따른 조건
	 */
	private BooleanExpression couponTypeEq(String couponType) {
		return !Objects.equals(couponType, "ALL")
			? couponPolicy.couponType.name.eq(CouponScope.fromString(couponType)) : null;
	}
}
