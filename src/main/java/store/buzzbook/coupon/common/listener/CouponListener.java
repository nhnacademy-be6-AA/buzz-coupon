package store.buzzbook.coupon.common.listener;

import static store.buzzbook.coupon.common.constant.CouponPolicyConstant.*;

import java.time.LocalDate;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.coupon.common.constant.CouponScope;
import store.buzzbook.coupon.common.constant.DiscountType;
import store.buzzbook.coupon.common.exception.CouponTypeNotFoundException;
import store.buzzbook.coupon.entity.CouponPolicy;
import store.buzzbook.coupon.entity.CouponType;
import store.buzzbook.coupon.repository.CouponTypeRepository;
import store.buzzbook.coupon.repository.couponpolicy.CouponPolicyRepository;

/**
 * 애플리케이션 시작 시 쿠폰 타입과 쿠폰 정책을 초기화하는 리스너 클래스입니다.
 * <p>
 * ApplicationReadyEvent 이벤트를 수신하여 필요한 쿠폰 타입과 정책이 존재하지 않을 경우 이를 생성합니다.
 * </p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CouponListener implements ApplicationListener<ApplicationReadyEvent> {

	private final CouponTypeRepository couponTypeRepository;
	private final CouponPolicyRepository couponPolicyRepository;

	/**
	 * 애플리케이션이 시작될 때 호출되어 쿠폰 타입과 쿠폰 정책을 초기화합니다.
	 *
	 * @param event 애플리케이션 준비 완료 이벤트
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {

		String action = "Created coupon type {}";
		if (Boolean.FALSE.equals(couponTypeRepository.existsByName(CouponScope.GLOBAL))) {
			couponTypeRepository.save(CouponType.builder()
				.name(CouponScope.GLOBAL)
				.build());
			log.debug(action, CouponScope.GLOBAL);
		}

		if (Boolean.FALSE.equals(couponTypeRepository.existsByName(CouponScope.BOOK))) {
			couponTypeRepository.save(CouponType.builder()
				.name(CouponScope.BOOK)
				.build());
			log.debug(action, CouponScope.BOOK);
		}

		if (Boolean.FALSE.equals(couponTypeRepository.existsByName(CouponScope.CATEGORY))) {
			couponTypeRepository.save(CouponType.builder()
				.name(CouponScope.CATEGORY)
				.build());
			log.debug(action, CouponScope.CATEGORY);
		}

		CouponType globalType = couponTypeRepository.findAllByName(CouponScope.GLOBAL)
			.orElseThrow(CouponTypeNotFoundException::new);

		if (Boolean.FALSE.equals(couponPolicyRepository.existsByName(WELCOME_COUPON_POLICY_NAME))) {
			couponPolicyRepository.save(CouponPolicy.builder()
				.name(WELCOME_COUPON_POLICY_NAME)
				.discountType(DiscountType.AMOUNT)
				.discountRate(0)
				.discountAmount(10000)
				.maxDiscountAmount(0)
				.standardPrice(10000)
				.startDate(LocalDate.EPOCH)
				.endDate(LocalDate.of(2099, 12, 31))
				.period(30)
				.deleted(false)
				.couponType(globalType)
				.build());
		}
	}
}
