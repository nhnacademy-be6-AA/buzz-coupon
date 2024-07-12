package store.buzzbook.coupon.service;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.common.exception.CouponPolicyNotFoundException;
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
import store.buzzbook.coupon.service.impl.CouponPolicyServiceImpl;

@ExtendWith(MockitoExtension.class)
class CouponPolicyServiceTest {

	@Mock
	private CouponPolicyRepository couponPolicyRepository;

	@Mock
	private CouponTypeService couponTypeService;

	@Mock
	private SpecificCouponRepository specificCouponRepository;

	@Mock
	private CategoryCouponRepository categoryCouponRepository;

	@InjectMocks
	private CouponPolicyServiceImpl couponPolicyService;

	private CouponPolicy testCouponPolicy1;
	private CouponPolicy testCouponPolicy2;

	@BeforeEach
	void setUp() {
		CouponType testCouponType1 = CouponType.builder()
			.name(CouponScope.GLOBAL)
			.build();

		CouponType testCouponType2 = CouponType.builder()
			.name(CouponScope.BOOK)
			.build();

		testCouponPolicy1 = CouponPolicy.builder()
			.id(1)
			.couponType(testCouponType2)
			.standardPrice(1000)
			.discountType(DiscountType.AMOUNT)
			.discountAmount(10000)
			.discountRate(1.0)
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.name("test")
			.maxDiscountAmount(10000)
			.deleted(false)
			.build();

		testCouponPolicy2 = CouponPolicy.builder()
			.id(1)
			.couponType(testCouponType1)
			.standardPrice(1000)
			.discountType(DiscountType.AMOUNT)
			.discountAmount(10000)
			.discountRate(1.0)
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(1))
			.name("생일 쿠폰 test")
			.maxDiscountAmount(10000)
			.deleted(false)
			.build();
	}

	@Test
	@DisplayName("get coupon policies by paging")
	void getCouponPoliciesByPaging() {
		// given
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
		CouponPolicyConditionRequest testCondition = new CouponPolicyConditionRequest(
			"AMOUNT",
			"false",
			"BOOK"
		);
		when(couponPolicyRepository.findAllByCondition(any(), any())).thenReturn(
			new PageImpl<>(List.of(testCouponPolicy1)));

		// when
		Page<CouponPolicyResponse> couponPolicies = couponPolicyService.getCouponPoliciesByPaging(pageable,
			testCondition);

		// then
		assertThat(couponPolicies).isNotNull();
		assertEquals(1, couponPolicies.getTotalElements());
	}

	@Test
	@DisplayName("get coupon policies by paging with illegalArgumentException")
	void getCouponPoliciesByPagingWithIllegalArgumentException() {
		// given

		// when & then
		assertThrows(IllegalArgumentException.class, () -> couponPolicyService.getCouponPoliciesByPaging(null, null));
	}

	@Test
	@DisplayName("get coupon policies by book scope")
	void getCouponPoliciesByBookScope() {
		// given
		List<String> testScopes = List.of("BOOK");
		when(couponPolicyRepository.findAllByCouponScope(any())).thenReturn(List.of(testCouponPolicy1));

		// when
		CouponPoliciesResponse couponPolicyResponse = couponPolicyService.getCouponPoliciesByScope(testScopes);

		// then
		assertThat(couponPolicyResponse).isNotNull();
		assertEquals(1, couponPolicyResponse.specificCouponPolicies().size());
	}

	@Test
	@DisplayName("get coupon policies by global scope")
	void getCouponPoliciesByGlobalScope() {
		// given
		List<String> testScopes = List.of("GlOBAL");
		when(couponPolicyRepository.findAllByCouponScope(any())).thenReturn(List.of(testCouponPolicy2));

		// when
		CouponPoliciesResponse couponPoliciesResponse = couponPolicyService.getCouponPoliciesByScope(testScopes);

		// then
		assertThat(couponPoliciesResponse).isNotNull();
		assertEquals(0, couponPoliciesResponse.specificCouponPolicies().size());
	}

	@Test
	@DisplayName("get coupon policies by scope with couponPolicyNotFoundException")
	void getCouponPoliciesByScopeWithCouponPolicyNotFoundException() {
		// given

		// when & then
		assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.getCouponPoliciesByScope(null));
		assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.getCouponPoliciesByScope(
			Collections.emptyList()));
	}

	@Test
	@DisplayName("get specific coupons by bookId")
	void getSpecificCoupons() {
		// given
		when(couponPolicyRepository.findAllByBookId(anyInt())).thenReturn(List.of(testCouponPolicy1));

		// when
		List<CouponPolicyResponse> result = couponPolicyService.getSpecificCoupons(1);

		// then
		assertEquals(1, result.size());
		assertEquals(testCouponPolicy1.getId(), result.getFirst().id());
		verify(couponPolicyRepository, times(1)).findAllByBookId(anyInt());
	}

	@Test
	@DisplayName("create book scope coupon policy")
	void createBookScopeCouponPolicy() {
		// given
		CreateCouponPolicyRequest testRequest = new CreateCouponPolicyRequest(
			"test",
			"rate",
			1.0,
			3000,
			10000,
			10000,
			14,
			LocalDate.now().toString(),
			LocalDate.now().plusDays(10).toString(),
			"book",
			1
		);

		SpecificCoupon testSpecificCoupon = SpecificCoupon.builder()
			.couponPolicy(testCouponPolicy1)
			.bookId(testRequest.targetId())
			.build();

		when(couponTypeService.getCouponType(anyString())).thenReturn(
			CouponType.builder().name(CouponScope.BOOK).build());
		when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(testCouponPolicy1);
		when(specificCouponRepository.save(any(SpecificCoupon.class))).thenReturn(testSpecificCoupon);

		// when
		CreateCouponPolicyResponse result = couponPolicyService.createCouponPolicy(testRequest);

		// then
		assertEquals(testRequest.name(), result.name());
	}

	@Test
	@DisplayName("create category scope coupon policy")
	void createCategoryScopeCouponPolicy() {
		// given
		CreateCouponPolicyRequest testRequest = new CreateCouponPolicyRequest(
			"카테고리 쿠폰 test",
			"rate",
			1.0,
			3000,
			10000,
			10000,
			14,
			LocalDate.now().toString(),
			LocalDate.now().plusDays(10).toString(),
			"category",
			1
		);

		CouponType testCouponType3 = CouponType.builder()
			.name(CouponScope.CATEGORY)
			.build();

		CouponPolicy testCouponPolicy3 = CouponPolicy.builder()
			.couponType(testCouponType3)
			.standardPrice(testRequest.standardPrice())
			.discountType(DiscountType.AMOUNT)
			.discountAmount(testRequest.discountAmount())
			.discountRate(testRequest.discountRate())
			.startDate(LocalDate.parse(testRequest.startDate()))
			.endDate(LocalDate.parse(testRequest.endDate()))
			.name("카테고리 쿠폰 test")
			.maxDiscountAmount(testRequest.maxDiscountAmount())
			.deleted(false)
			.build();

		CategoryCoupon testCategoryCoupon = CategoryCoupon.builder()
			.couponPolicy(testCouponPolicy3)
			.categoryId(testRequest.targetId())
			.build();

		when(couponTypeService.getCouponType(anyString())).thenReturn(
			CouponType.builder().name(CouponScope.CATEGORY).build());
		when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(testCouponPolicy3);
		when(categoryCouponRepository.save(any(CategoryCoupon.class))).thenReturn(testCategoryCoupon);

		// when
		CreateCouponPolicyResponse result = couponPolicyService.createCouponPolicy(testRequest);

		// then
		assertEquals(testRequest.name(), result.name());
	}

	@Test
	@DisplayName("create IllegalArgumentException")
	void createIllegalArgumentException() {
		// given

		// when & then
		assertThrows(IllegalArgumentException.class, () -> couponPolicyService.createCouponPolicy(null));
	}

	@Test
	@DisplayName("update")
	void update() {
		// given
		UpdateCouponPolicyRequest testRequest = new UpdateCouponPolicyRequest(LocalDate.now().plusDays(10));
		when(couponPolicyRepository.findById(anyInt())).thenReturn(Optional.of(testCouponPolicy1));

		// when
		couponPolicyService.updateCouponPolicy(1, testRequest);

		// then
		verify(couponPolicyRepository, times(1)).findById(anyInt());
	}

	@Test
	@DisplayName("update IllegalArgumentException")
	void updateIllegalArgumentException() {
		// given
		UpdateCouponPolicyRequest testRequest = new UpdateCouponPolicyRequest(LocalDate.now().plusDays(10));

		// when & then
		assertAll(
			() -> assertThrows(IllegalArgumentException.class,
				() -> couponPolicyService.updateCouponPolicy(0, testRequest)),
			() -> assertThrows(IllegalArgumentException.class,
				() -> couponPolicyService.updateCouponPolicy(-1, testRequest)),
			() -> assertThrows(IllegalArgumentException.class, () -> couponPolicyService.updateCouponPolicy(1, null))
		);
	}

	@Test
	@DisplayName("update coupon policy not found exception")
	void updateCouponPolicyNotFoundException() {
		// given
		UpdateCouponPolicyRequest testRequest = new UpdateCouponPolicyRequest(LocalDate.now().plusDays(10));
		when(couponPolicyRepository.findById(anyInt())).thenReturn(Optional.empty());

		// when & then
		assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.updateCouponPolicy(1, testRequest));
	}

	@Test
	@DisplayName("delete")
	void delete() {
		// given
		when(couponPolicyRepository.existsById(anyInt())).thenReturn(true);
		when(couponPolicyRepository.findById(anyInt())).thenReturn(Optional.of(testCouponPolicy1));

		// when
		couponPolicyService.deleteCouponPolicy(1);

		// then
		verify(couponPolicyRepository, times(1)).existsById(anyInt());
		verify(couponPolicyRepository, times(1)).findById(anyInt());
	}

	@Test
	@DisplayName("delete with IllegalArgumentException")
	void deleteIllegalArgumentException() {
		// given

		// when & then
		assertAll(
			() -> assertThrows(IllegalArgumentException.class, () -> couponPolicyService.deleteCouponPolicy(-1)),
			() -> assertThrows(IllegalArgumentException.class, () -> couponPolicyService.deleteCouponPolicy(0))
		);
	}

	@Test
	@DisplayName("delete with CouponPolicyNotFoundException")
	void deleteCouponPolicyNotFoundException() {
		// given
		when(couponPolicyRepository.existsById(anyInt())).thenReturn(false);

		// when & then
		assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.deleteCouponPolicy(1));
	}
}

