package store.buzzbook.coupon.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import store.buzzbook.coupon.entity.CategoryCoupon;
import store.buzzbook.coupon.entity.CouponLog;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.entity.constant.CouponRange;
import store.buzzbook.coupon.entity.constant.CouponStatus;
import store.buzzbook.coupon.entity.constant.DiscountType;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryCouponRepositoryTest {

	@Autowired
	private CategoryCouponRepository categoryCouponRepository;

	@Autowired
	private CouponLogRepository couponLogRepository;

	@Autowired
	private CouponPolicyRepository couponPolicyRepository;

	private CouponType testCouponType;
	private CouponLog testCouponLog;
	private CouponPolicy testCouponPolicy;
	private CategoryCoupon testCategoryCoupon;
	@Autowired
	private CouponTypeRepository couponTypeRepository;

	@BeforeEach
	public void setUp() {
		testCouponType = CouponType.builder()
			.name(CouponRange.BOOK)
			.build();

		testCouponPolicy = CouponPolicy.builder()
			.couponType(testCouponType)
			.standardPrice(1000)
			.discountType(DiscountType.AMOUNT)
			.discountAmount(10000)
			.discountRate(1.0)
			.startDate(ZonedDateTime.now())
			.endDate(ZonedDateTime.now().plusDays(1))
			.name("test")
			.maxDiscountAmount(10000)
			.build();

		testCouponLog = CouponLog.builder()
			.couponPolicy(testCouponPolicy)
			.createDate(ZonedDateTime.now())
			.expireDate(ZonedDateTime.now().plusDays(2))
			.status(CouponStatus.AVAILABLE)
			.userId(1L)
			.build();

		testCategoryCoupon = CategoryCoupon.builder()
			.couponPolicy(testCouponPolicy)
			.categoryId(1)
			.build();

		couponTypeRepository.save(testCouponType);
		couponPolicyRepository.save(testCouponPolicy);
		categoryCouponRepository.save(testCategoryCoupon);
	}

	@Test
	@DisplayName("save")
	void save() {
		// given
		CategoryCoupon newCategoryCoupon = CategoryCoupon.builder()
			.couponPolicy(testCouponPolicy)
			.categoryId(1)
			.build();

		// when
		CategoryCoupon savedCategoryCoupon = categoryCouponRepository.save(newCategoryCoupon);

		//then
		assertEquals(newCategoryCoupon.getCouponPolicy(), savedCategoryCoupon.getCouponPolicy());
		assertEquals(newCategoryCoupon.getCategoryId(), savedCategoryCoupon.getCategoryId());
	}

	@Test
	@DisplayName("delete")
	void delete() {
		// given
		CategoryCoupon savedCategoryCoupon = categoryCouponRepository.findById(testCategoryCoupon.getId()).orElse(null);

		// when
		assert savedCategoryCoupon != null;
		categoryCouponRepository.delete(savedCategoryCoupon);
		Optional<CategoryCoupon> optionalCategoryCoupon = categoryCouponRepository.findById(testCategoryCoupon.getId());

		// then
		assertFalse(optionalCategoryCoupon.isPresent());
	}

	@Test
	@DisplayName("exist by category id")
	void existByCategoryId() {
		// given

		// when
		boolean exists = categoryCouponRepository.existsByCategoryId(testCategoryCoupon.getCategoryId());
		boolean notExists = categoryCouponRepository.existsByCategoryId(2);

		// then
		assertTrue(exists);
		assertFalse(notExists);
	}
}
