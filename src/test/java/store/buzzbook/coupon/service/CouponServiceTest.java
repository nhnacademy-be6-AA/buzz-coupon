package store.buzzbook.coupon.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.repository.CouponRepository;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;
import store.buzzbook.coupon.service.impl.CouponServiceImpl;

@ExtendWith(MockitoExtension.class)
@Transactional
class CouponServiceTest {

	@Mock
	private CouponRepository couponRepository;

	@Mock
	CouponPolicyRepository couponPolicyRepository;

	@Mock
	private CouponPolicyService couponPolicyService;

	@InjectMocks
	private CouponServiceImpl couponService;

	private Coupon testCoupon;
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

		testCoupon = Coupon.builder()
			.id(1L)
			.couponCode(CodeCreator.createCode())
			.couponPolicy(testCouponPolicy)
			.createDate(LocalDate.now())
			.expireDate(LocalDate.now().plusDays(7))
			.status(CouponStatus.AVAILABLE)
			.build();
	}

	@Test
	@DisplayName("get coupon")
	void getCoupon() {
		// given
		when(couponRepository.findById(anyLong())).thenReturn(Optional.of(testCoupon));

		// when
		CouponResponse couponResponse = couponService.getCoupon(1);

		// then
		assertNotNull(couponResponse);
		assertEquals(testCoupon.getId(), couponResponse.id());
		assertEquals(testCoupon.getCouponPolicy().getId(), couponResponse.couponPolicyResponse().id());
	}

	@Test
	@DisplayName("get coupon with illegalArgumentException")
	void getCouponWithIllegalArgumentException() {
		// given

		// when & then
		assertAll(
			() -> assertThrows(IllegalArgumentException.class, () -> couponService.getCoupon(-1)),
			() -> assertThrows(IllegalArgumentException.class, () -> couponService.getCoupon(0))
		);

	}

	@Test
	@DisplayName("get all coupons by all status")
	void getAllCouponsByAllStatus() {
		// given
		List<CouponLogRequest> testRequests = List.of(new CouponLogRequest(testCoupon.getCouponCode(),
			testCouponPolicy.getId()));
		String testCouponStatusName = "all";
		when(couponRepository.findByCouponCodeAndCouponPolicyId(anyString(), anyInt())).thenReturn(
			Optional.of(testCoupon));

		// when
		List<CouponResponse> couponResponses = couponService.getAllCouponsByStatus(testRequests, testCouponStatusName);

		// then
		assertNotNull(couponResponses);
		assertEquals(testRequests.size(), couponResponses.size());
	}

	@Test
	@DisplayName("get all coupons by status")
	void getAllCouponsByStatus() {
		// given
		List<CouponLogRequest> testRequests = List.of(new CouponLogRequest(testCoupon.getCouponCode(),
			testCouponPolicy.getId()));
		String testCouponStatusName = "available";
		when(couponRepository.findByCouponCodeAndCouponPolicyIdAndStatus(anyString(), anyInt(), any())).thenReturn(
			Optional.of(testCoupon));

		// when
		List<CouponResponse> couponResponses = couponService.getAllCouponsByStatus(testRequests, testCouponStatusName);

		// then
		assertNotNull(couponResponses);
		assertEquals(testRequests.size(), couponResponses.size());
	}

	@Test
	@DisplayName("get all coupons by status with exception")
	void getAllCouponsByStatusWithException() {
		// given
		List<CouponLogRequest> testRequests = List.of(new CouponLogRequest(testCoupon.getCouponCode(),
			testCouponPolicy.getId()));
		String testCouponStatusName = "all";

		// when & then
		assertAll(
			() -> assertTrue(
				couponService.getAllCouponsByStatus(Collections.emptyList(), testCouponStatusName).isEmpty()),
			() -> assertTrue(couponService.getAllCouponsByStatus(null, testCouponStatusName).isEmpty()),
			() -> assertThrows(IllegalArgumentException.class,
				() -> couponService.getAllCouponsByStatus(testRequests, null))
		);
	}

	@Test
	@DisplayName("get available coupons")
	void getAvailableCoupons() {
		// give
		OrderCouponResponse response = OrderCouponResponse.from(testCoupon);
		List<CouponLogRequest> testRequests = List.of(new CouponLogRequest(testCoupon.getCouponCode(),
			testCouponPolicy.getId()));
		when(couponPolicyRepository.findCouponsWithTargetId(anyString())).thenReturn(response);

		// when
		List<OrderCouponResponse> couponResponses = couponService.getAvailableCoupons(testRequests);

		// then
		assertNotNull(couponResponses);
		assertEquals(1, couponResponses.size());
	}

	@Test
	@DisplayName("get available coupons with exception")
	void getAvailableCouponsWithException() {
		// given

		// when & then
		assertAll(
			() -> assertTrue(couponService.getAvailableCoupons(Collections.emptyList()).isEmpty()),
			() -> assertTrue(couponService.getAvailableCoupons(null).isEmpty())
		);
	}

	@Test
	@DisplayName("save")
	void save() {
		// given
		CreateCouponRequest testRequest = new CreateCouponRequest(
			1
		);

		when(couponPolicyService.getCouponPolicyById(anyInt())).thenReturn(testCouponPolicy);
		when(couponRepository.save(any())).thenReturn(testCoupon);

		// when
		CreateCouponResponse testResponse = couponService.createCoupon(testRequest);

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
		assertThrows(IllegalArgumentException.class, () -> couponService.createCoupon(null));
	}

	@Test
	@DisplayName("update coupon")
	void updateCoupon() {
		// given
		UpdateCouponRequest testRequest = new UpdateCouponRequest(testCoupon.getCouponCode(), CouponStatus.USED);
		when(couponRepository.findByCouponCode(anyString())).thenReturn(Optional.of(testCoupon));
		when(couponRepository.existsByCouponCode(anyString())).thenReturn(true);

		// when
		CouponResponse couponResponse = couponService.updateCoupon(testRequest);

		// then
		verify(couponRepository, times(1)).findByCouponCode(anyString());
		assertEquals(CouponStatus.USED, couponResponse.status());
	}

	@Test
	@DisplayName("update coupon with exception")
	void updateCouponWithIllegalArgumentException() {
		// given
		UpdateCouponRequest testRequest = new UpdateCouponRequest(testCoupon.getCouponCode(), CouponStatus.USED);
		when(couponRepository.existsByCouponCode(anyString())).thenReturn(Boolean.FALSE);

		// when & then
		assertAll(
			() -> assertThrows(IllegalArgumentException.class, () -> couponService.updateCoupon(null)),
			() -> assertThrows(CouponNotFoundException.class, () -> couponService.updateCoupon(testRequest))
		);
	}
}
