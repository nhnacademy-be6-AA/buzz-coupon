package store.buzzbook.coupon.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.CouponStatus;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.dto.coupon.CouponLogRequest;
import store.buzzbook.coupon.dto.coupon.CouponResponse;
import store.buzzbook.coupon.dto.coupon.CreateCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateCouponResponse;
import store.buzzbook.coupon.dto.coupon.OrderCouponResponse;
import store.buzzbook.coupon.dto.coupon.UpdateCouponRequest;
import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.entity.Coupon;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.service.CouponService;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CouponService couponService;

	private Coupon testCoupon;
	private CouponResponse testCouponResponse;
	private CreateCouponResponse testCreateCouponResponse;
	private OrderCouponResponse testOrderCouponResponse;
	private List<CouponLogRequest> testCouponLogRequests;

	@BeforeEach
	void setUp() {
		CouponType testCouponType = CouponType.builder()
			.id(1)
			.name(CouponScope.BOOK)
			.build();

		CouponPolicy testCouponPolicy = CouponPolicy.builder()
			.id(1)
			.couponType(testCouponType)
			.standardPrice(1000)
			.discountType(DiscountType.AMOUNT)
			.discountAmount(3000)
			.discountRate(1.0)
			.period(14)
			.startDate(LocalDate.now())
			.endDate(LocalDate.now().plusDays(10))
			.name("test")
			.maxDiscountAmount(10000)
			.deleted(false)
			.build();

		testCoupon = Coupon.builder()
			.couponCode("aaa")
			.couponPolicy(testCouponPolicy)
			.createDate(LocalDate.now())
			.expireDate(LocalDate.now().plusDays(14))
			.build();

		testCouponResponse = CouponResponse.from(testCoupon);
		testCreateCouponResponse = CreateCouponResponse.from(testCoupon);
		testOrderCouponResponse = new OrderCouponResponse(
			"aaa", LocalDate.now(), LocalDate.now().plusDays(14), CouponStatus.AVAILABLE,
			CouponPolicyResponse.from(testCouponPolicy), 1);
		testCouponLogRequests = List.of(new CouponLogRequest("aaa", 1));
	}

	@Test
	@DisplayName("get coupon by ID")
	void getCoupon() throws Exception {
		when(couponService.getCoupon(anyLong())).thenReturn(testCouponResponse);

		mockMvc.perform(get("/api/coupons/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(testCouponResponse.id()));

		verify(couponService).getCoupon(anyLong());
	}

	@Test
	@DisplayName("get available user coupons")
	void getAvailableUserCoupons() throws Exception {
		when(couponService.getAvailableCoupons(anyList())).thenReturn(List.of(testOrderCouponResponse));

		mockMvc.perform(post("/api/coupons/order")
				.content(objectMapper.writeValueAsString(testCouponLogRequests))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].code").value(testOrderCouponResponse.code()))
			.andExpect(jsonPath("$[0].targetId").value(testOrderCouponResponse.targetId()));

		verify(couponService).getAvailableCoupons(anyList());
	}

	@Test
	@DisplayName("get user coupons by condition")
	void getUserCouponsByCondition() throws Exception {
		when(couponService.getAllCouponsByStatus(anyList(), anyString())).thenReturn(List.of(testCouponResponse));

		mockMvc.perform(post("/api/coupons/condition")
				.param("couponStatusName", "AVAILABLE")
				.content(objectMapper.writeValueAsString(testCouponLogRequests))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(testCouponResponse.id()));

		verify(couponService).getAllCouponsByStatus(anyList(), anyString());
	}

	@Test
	@DisplayName("create coupon")
	void createCoupon() throws Exception {
		CreateCouponRequest request = new CreateCouponRequest(1);

		when(couponService.createCoupon(any())).thenReturn(testCreateCouponResponse);

		mockMvc.perform(post("/api/coupons")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(testCreateCouponResponse.id()))
			.andExpect(jsonPath("$.couponCode").value(testCreateCouponResponse.couponCode()));

		verify(couponService).createCoupon(any());
	}

	@Test
	@DisplayName("update coupon")
	void updateCoupon() throws Exception {
		UpdateCouponRequest request = new UpdateCouponRequest("abc", CouponStatus.USED);

		when(couponService.updateCoupon(any())).thenReturn(testCouponResponse);

		mockMvc.perform(put("/api/coupons")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(testCouponResponse.id()));

		verify(couponService).updateCoupon(any());
	}
}
