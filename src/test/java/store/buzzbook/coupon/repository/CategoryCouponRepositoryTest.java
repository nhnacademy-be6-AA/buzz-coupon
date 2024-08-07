package store.buzzbook.coupon.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.entity.CategoryCoupon;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryCouponRepositoryTest {

	@Autowired
	private CategoryCouponRepository categoryCouponRepository;

	@Autowired
	private CouponPolicyRepository couponPolicyRepository;

	@Autowired
	private CouponTypeRepository couponTypeRepository;

	private CouponPolicy testCouponPolicy;
	private CategoryCoupon testCategoryCoupon;

	@BeforeEach
	public void setUp() {
		CouponType testCouponType = CouponType.builder()
			.name(CouponScope.BOOK)
			.build();

		testCouponPolicy = CouponPolicy.builder()
			.couponType(testCouponType)
			.standardPrice(1000)
			.discountType(DiscountType.AMOUNT)
			.discountAmount(10000)
			.discountRate(1.0)
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.name("test")
			.maxDiscountAmount(10000)
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
			.categoryId(2)
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
}
