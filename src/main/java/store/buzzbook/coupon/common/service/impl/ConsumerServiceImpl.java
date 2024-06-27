package store.buzzbook.coupon.common.service.impl;

import static store.buzzbook.coupon.common.config.RabbitmqConfig.*;
import static store.buzzbook.coupon.common.constant.CouponPolicyConstant.*;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.coupon.common.exception.CouponPolicyNotFoundException;
import store.buzzbook.coupon.dto.coupon.CreateCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateCouponResponse;
import store.buzzbook.coupon.dto.coupon.CreateWelcomeCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateWelcomeCouponResponse;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;
import store.buzzbook.coupon.service.CouponService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl {

	private final CouponProducerServiceImpl couponProducerService;
	private final CouponPolicyRepository couponPolicyRepository;
	private final CouponService couponService;

	@Transactional
	@RabbitListener(queues = REQUEST_QUEUE_NAME)
	public void receiveWelcomeCouponRequest(CreateWelcomeCouponRequest request) {
		log.info("Received request: {}", request);

		CouponPolicy welcomeCouponPolicy = couponPolicyRepository.findByName(WELCOME_COUPON_POLICY_NAME)
			.orElseThrow(CouponPolicyNotFoundException::new);

		CreateCouponResponse savedCoupon = couponService.createCoupon(CreateCouponRequest.builder()
			.couponPolicyId(welcomeCouponPolicy.getId())
			.build());

		couponProducerService.sendWelcomeCouponResponse(CreateWelcomeCouponResponse.builder()
			.resultCode(HttpStatus.OK.value())
			.userId(request.userId())
			.couponId(savedCoupon.id())
			.build());
	}
}
