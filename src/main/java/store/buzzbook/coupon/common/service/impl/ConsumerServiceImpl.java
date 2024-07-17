package store.buzzbook.coupon.common.service.impl;

import static store.buzzbook.coupon.common.constant.CouponPolicyConstant.*;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.coupon.adapter.UserAdapter;
import store.buzzbook.coupon.common.exception.CouponPolicyNotFoundException;
import store.buzzbook.coupon.dto.coupon.CreateCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateCouponResponse;
import store.buzzbook.coupon.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.coupon.dto.coupon.CreateWelcomeCouponRequest;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;
import store.buzzbook.coupon.service.CouponService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl {

	@Value("${spring.rabbitmq.coupon.queue}")
	private String queueName;

	@Value("${spring.rabbitmq.coupon.routing-key}")
	private String routingKey;

	private final CouponPolicyRepository couponPolicyRepository;
	private final UserAdapter userAdapter;
	private final CouponService couponService;

	private final RabbitTemplate rabbitTemplate;

	@Transactional
	@RabbitListener(queues = "${spring.rabbitmq.coupon.queue}")
	public void receiveWelcomeCouponRequest(CreateWelcomeCouponRequest request) {
		log.debug("Received WelcomeCouponRequest: {}", request);
		CouponPolicy welcomeCouponPolicy = couponPolicyRepository.findByName(WELCOME_COUPON_POLICY_NAME)
			.orElseThrow(CouponPolicyNotFoundException::new);

		CreateCouponResponse savedCoupon = couponService.createCoupon(CreateCouponRequest.builder()
			.couponPolicyId(welcomeCouponPolicy.getId())
			.build());

		userAdapter.createUserCoupon(CreateUserCouponRequest.builder()
			.userId(request.userId())
			.couponPolicyId(savedCoupon.couponPolicyResponse().id())
			.couponCode(savedCoupon.couponCode())
			.build());
	}

	@RabbitListener(queues = "${spring.rabbitmq.coupon.dlx.queue}")
	public void handleDlqMessage(CreateWelcomeCouponRequest request) {
		log.debug("Received DlqMessage: {}", request);

		rabbitTemplate.convertAndSend(queueName, routingKey, request);
	}
}
