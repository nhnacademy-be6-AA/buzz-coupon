package store.buzzbook.coupon.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.coupon.dto.couponpolicy.CouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyRequest;
import store.buzzbook.coupon.dto.couponpolicy.CreateCouponPolicyResponse;
import store.buzzbook.coupon.dto.couponpolicy.UpdateCouponPolicyRequest;
import store.buzzbook.coupon.dto.coupontype.CouponTypeResponse;
import store.buzzbook.coupon.dto.coupontype.CreateCouponTypeRequest;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.common.constant.CouponRange;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.service.CouponPolicyService;
import store.buzzbook.coupon.service.CouponTypeService;

@WebMvcTest(CouponPolicyController.class)
class CouponPolicyControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CouponPolicyService couponPolicyService;

	@MockBean
	private CouponTypeService couponTypeService;

	private CouponTypeResponse testCouponTypeResponse;
	private CouponPolicyResponse testCouponPolicyResponse;
	private CreateCouponPolicyResponse testCreateCouponPolicyResponse;
	private Page<CouponPolicyResponse> couponPolicyPage;

	@BeforeEach
	void setUp() {
		CouponType testCouponType = CouponType.builder()
			.id(1)
			.name(CouponRange.BOOK)
			.build();

		testCouponTypeResponse = CouponTypeResponse.from(testCouponType);

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
			.isDeleted(false)
			.build();

		testCouponPolicyResponse = CouponPolicyResponse.from(testCouponPolicy);
		testCreateCouponPolicyResponse = new CreateCouponPolicyResponse(1, "test");

		couponPolicyPage = new PageImpl<>(List.of(testCouponPolicyResponse));
	}

	@Test
	@DisplayName("get coupon types")
	void getCouponTypes() throws Exception {
		// given
		when(couponTypeService.getAllCouponTypes()).thenReturn(List.of(testCouponTypeResponse));

		// when & then
		mockMvc.perform(get("/api/coupons/policies/types"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(testCouponTypeResponse.id()))
			.andExpect(jsonPath("$[0].name").value(testCouponTypeResponse.name().toString()));

		verify(couponTypeService).getAllCouponTypes();
	}

	@Test
	@DisplayName("create coupon type")
	void createCouponType() throws Exception {
		// given
		CreateCouponTypeRequest request = new CreateCouponTypeRequest(CouponRange.BOOK);
		when(couponTypeService.createCouponType(any())).thenReturn(testCouponTypeResponse);

		// when & then
		mockMvc.perform(post("/api/coupons/policies/types")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(testCouponTypeResponse.id()))
			.andExpect(jsonPath("$.name").value(testCouponTypeResponse.name().toString()));

		verify(couponTypeService).createCouponType(any());
	}

	@Test
	@DisplayName("create coupon type with invalid")
	void createCouponTypeInvalid() throws Exception {
		// given
		CreateCouponTypeRequest request = new CreateCouponTypeRequest(null);

		// when & then
		mockMvc.perform(post("/api/coupons/policies/types")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").exists());

		verify(couponTypeService, never()).createCouponType(any());
	}

	@Test
	@DisplayName("delete coupon type")
	void deleteCouponType() throws Exception {
		// given
		doNothing().when(couponTypeService).deleteCouponType(anyInt());

		// when & then
		mockMvc.perform(delete("/api/coupons/policies/types/1"))
			.andExpect(status().isNoContent());

		verify(couponTypeService).deleteCouponType(anyInt());
	}

	@Test
	@DisplayName("get coupon policies by paging")
	void getCouponPoliciesByPaging() throws Exception {
		// given
		when(couponPolicyService.getCouponPoliciesByPaging(any(Pageable.class))).thenReturn(couponPolicyPage);

		// when & then
		mockMvc.perform(get("/api/coupons/policies").param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].id").value(testCouponPolicyResponse.id()))
			.andExpect(jsonPath("$.content[0].name").value(testCouponPolicyResponse.name()));

		verify(couponPolicyService).getCouponPoliciesByPaging(any(Pageable.class));
	}

	@Test
	@DisplayName("create coupon policy")
	void createCouponPolicy() throws Exception {
		// given
		CreateCouponPolicyRequest request = new CreateCouponPolicyRequest(
			"test",
			"amount",
			1.0,
			3000,
			10000,
			10000,
			14,
			ZonedDateTime.now().toString(),
			ZonedDateTime.now().plusDays(10).toString(),
			"book",
			false,
			1
		);

		when(couponPolicyService.createCouponPolicy(any())).thenReturn(testCreateCouponPolicyResponse);

		// when & then
		mockMvc.perform(post("/api/coupons/policies")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(testCreateCouponPolicyResponse.id()))
			.andExpect(jsonPath("$.name").value(testCreateCouponPolicyResponse.name()));

		verify(couponPolicyService).createCouponPolicy(any());
	}

	@Test
	@DisplayName("update coupon policy")
	void updateCouponPolicy() throws Exception {
		// given
		UpdateCouponPolicyRequest request = new UpdateCouponPolicyRequest(LocalDate.now().plusDays(10));
		when(couponPolicyService.updateCouponPolicy(anyInt(), any())).thenReturn(testCouponPolicyResponse);

		// when & then
		mockMvc.perform(put("/api/coupons/policies/1")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(testCouponPolicyResponse.id()))
			.andExpect(jsonPath("$.name").value(testCouponPolicyResponse.name()));

		verify(couponPolicyService).updateCouponPolicy(anyInt(), any());
	}

	@Test
	@DisplayName("delete coupon policy")
	void deleteCouponPolicy() throws Exception {
		// given
		doNothing().when(couponPolicyService).deleteCouponPolicy(anyInt());

		// when & then
		mockMvc.perform(delete("/api/coupons/policies/1"))
			.andExpect(status().isNoContent());

		verify(couponPolicyService).deleteCouponPolicy(anyInt());
	}

	@Test
	@DisplayName("get specific coupons by bookId")
	void getSpecificCoupons() throws Exception {
		// given
		when(couponPolicyService.getSpecificCoupons(anyInt())).thenReturn(List.of(testCouponPolicyResponse));

		// when & then
		mockMvc.perform(get("/api/coupons/policies/specifics/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(testCouponPolicyResponse.id()))
			.andExpect(jsonPath("$[0].name").value(testCouponPolicyResponse.name()));

		verify(couponPolicyService).getSpecificCoupons(anyInt());
	}
}
