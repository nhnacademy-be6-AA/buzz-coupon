package store.buzzbook.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.dto.coupon.CreateCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateCouponResponse;
import store.buzzbook.coupon.entity.Coupon;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.repository.CouponRepository;
import store.buzzbook.coupon.service.impl.CouponServiceImpl;

@ExtendWith(MockitoExtension.class)
@Transactional
class CouponServiceTest {

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private CouponPolicyService couponPolicyService;

	@InjectMocks
	private CouponServiceImpl couponLogService;

	private Coupon testCouponLog;
	private CouponPolicy testCouponPolicy;

	@BeforeEach
	void setUp() {
		CouponType testCouponType = CouponType.builder()
			.name(CouponScope.BOOK)
			.build();

		testCouponPolicy = CouponPolicy.builder()
			.id(1)
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

		testCouponLog = Coupon.builder()
			.id(1L)
			.couponPolicy(testCouponPolicy)
			.createDate(LocalDate.now())
			.expireDate(LocalDate.now().plusDays(7))
			.status(CouponStatus.AVAILABLE)
			.build();
	}

	@Test
	@DisplayName("save")
	void save() {
		// given
		CreateCouponRequest testRequest = new CreateCouponRequest(
			1
		);

		when(couponPolicyService.getCouponPolicyById(anyInt())).thenReturn(testCouponPolicy);
		when(couponRepository.save(any())).thenReturn(testCouponLog);

		// when
		CreateCouponResponse testResponse = couponLogService.createCoupon(testRequest);

		// then
		verify(couponPolicyService, times(1)).getCouponPolicyById(anyInt());
		verify(couponRepository, times(1)).save(any());
		assertEquals(testRequest.couponPolicyId(), testResponse.couponPolicyResponse().id());
	}

	@Test
	@DisplayName("save IllegalArgumentException")
	void saveIllegalArgumentException() {
		// given

		// when & then
		assertThrows(IllegalArgumentException.class, () -> couponLogService.createCoupon(null));
	}
}
