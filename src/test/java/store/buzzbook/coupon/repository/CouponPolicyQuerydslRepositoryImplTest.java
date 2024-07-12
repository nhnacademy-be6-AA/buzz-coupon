package store.buzzbook.coupon.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.common.utils.CodeCreator;
import store.buzzbook.coupon.dto.coupon.OrderCouponResponse;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyConditionRequest;
import store.buzzbook.coupon.entity.CategoryCoupon;
import store.buzzbook.coupon.entity.Coupon;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.entity.SpecificCoupon;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CouponPolicyQuerydslRepositoryImplTest {

	@Autowired
	private CouponTypeRepository couponTypeRepository;

	@Autowired
	private CouponPolicyRepository couponPolicyRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private SpecificCouponRepository specificCouponRepository;

	@Autowired
	private CategoryCouponRepository categoryCouponRepository;

	private CouponPolicy testCouponPolicy1;
	private CouponPolicy testCouponPolicy3;
	private Coupon testCoupon1;
	private Coupon testCoupon2;

	@BeforeEach
	void setUp() {
		CouponType testCouponType1 = CouponType.builder()
			.name(CouponScope.BOOK)
			.build();
		couponTypeRepository.save(testCouponType1);

		CouponType testCouponType2 = CouponType.builder()
			.name(CouponScope.GLOBAL)
			.build();
		couponTypeRepository.save(testCouponType2);

		CouponType testCouponType3 = CouponType.builder()
			.name(CouponScope.CATEGORY)
			.build();
		couponTypeRepository.save(testCouponType3);

		testCouponPolicy1 = CouponPolicy.builder()
			.couponType(testCouponType1)
			.standardPrice(1000)
			.discountType(DiscountType.AMOUNT)
			.discountAmount(3000)
			.discountRate(0)
			.period(14)
			.startDate(LocalDate.now().minusDays(10))
			.endDate(LocalDate.now().plusDays(10))
			.name("test")
			.maxDiscountAmount(10000)
			.deleted(false)
			.build();
		couponPolicyRepository.save(testCouponPolicy1);

		CouponPolicy testCouponPolicy2 = CouponPolicy.builder()
			.couponType(testCouponType2)
			.standardPrice(1000)
			.discountType(DiscountType.RATE)
			.discountAmount(0)
			.discountRate(0.2)
			.period(14)
			.startDate(LocalDate.now().minusDays(10))
			.endDate(LocalDate.now().plusDays(10))
			.name("test")
			.maxDiscountAmount(10000)
			.deleted(false)
			.build();
		couponPolicyRepository.save(testCouponPolicy2);

		testCouponPolicy3 = CouponPolicy.builder()
			.couponType(testCouponType3)
			.standardPrice(1000)
			.discountType(DiscountType.RATE)
			.discountAmount(0)
			.discountRate(0.2)
			.period(14)
			.startDate(LocalDate.now().minusDays(10))
			.endDate(LocalDate.now().plusDays(10))
			.name("test")
			.maxDiscountAmount(10000)
			.deleted(false)
			.build();
		couponPolicyRepository.save(testCouponPolicy3);

		SpecificCoupon testSpecificCoupon1 = SpecificCoupon.builder()
			.couponPolicy(testCouponPolicy1)
			.bookId(1)
			.build();
		specificCouponRepository.save(testSpecificCoupon1);

		SpecificCoupon testSpecificCoupon2 = SpecificCoupon.builder()
			.couponPolicy(testCouponPolicy2)
			.bookId(1)
			.build();
		specificCouponRepository.save(testSpecificCoupon2);

		CategoryCoupon testCategoryCoupon1 = CategoryCoupon.builder()
			.couponPolicy(testCouponPolicy3)
			.categoryId(1)
			.build();
		categoryCouponRepository.save(testCategoryCoupon1);

		testCoupon1 = Coupon.builder()
			.couponPolicy(testCouponPolicy1)
			.couponCode(CodeCreator.createCode())
			.status(CouponStatus.AVAILABLE)
			.createDate(LocalDate.now())
			.expireDate(LocalDate.now().plusDays(testCouponPolicy1.getPeriod()))
			.build();
		couponRepository.save(testCoupon1);

		testCoupon2 = Coupon.builder()
			.couponPolicy(testCouponPolicy3)
			.couponCode(CodeCreator.createCode())
			.status(CouponStatus.AVAILABLE)
			.createDate(LocalDate.now())
			.expireDate(LocalDate.now().plusDays(testCouponPolicy3.getPeriod()))
			.build();
		couponRepository.save(testCoupon2);
	}

	@Test
	@DisplayName("find all book id")
	void findAllByBookId() {
		// given

		// when
		List<CouponPolicy> result = couponPolicyRepository.findAllByBookId(1);

		// then
		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("find all by coupon scope")
	void findAllByCouponScope() {
		// given
		CouponScope testCouponScope = CouponScope.BOOK;

		// when
		List<CouponPolicy> result = couponPolicyRepository.findAllByCouponScope(testCouponScope);

		// then
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("find all by coupon condition")
	void findAllByCouponCondition() {
		// given
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
		CouponPolicyConditionRequest testCondition = new CouponPolicyConditionRequest(
			"RATE",
			"false",
			"GLOBAL"
		);

		// when
		Page<CouponPolicy> couponPolicies = couponPolicyRepository.findAllByCondition(pageable, testCondition);

		// then
		assertThat(couponPolicies).isNotNull();
		assertThat(couponPolicies.getContent()).isNotEmpty();
		assertEquals(1, couponPolicies.getContent().size());
	}

	@Test
	@DisplayName("find book coupon with target id")
	void findBookCouponWithTargetId() {
		// given
		String testCouponCode = testCoupon1.getCouponCode();

		// when
		OrderCouponResponse response = couponPolicyRepository.findCouponsWithTargetId(testCouponCode);

		// then
		assertEquals(testCouponCode, response.code());
		assertEquals(CouponStatus.AVAILABLE, response.status());
		assertEquals(testCouponPolicy1.getId(), response.couponPolicyResponse().id());
		assertEquals(1, response.targetId());
	}

	@Test
	@DisplayName("find category coupon with target id")
	void findCategoryCouponWithTargetId() {
		// given
		String testCouponCode = testCoupon2.getCouponCode();

		// when
		OrderCouponResponse response = couponPolicyRepository.findCouponsWithTargetId(testCouponCode);

		// then
		assertEquals(testCouponCode, response.code());
		assertEquals(CouponStatus.AVAILABLE, response.status());
		assertEquals(testCouponPolicy3.getId(), response.couponPolicyResponse().id());
		assertEquals(1, response.targetId());
	}
}
