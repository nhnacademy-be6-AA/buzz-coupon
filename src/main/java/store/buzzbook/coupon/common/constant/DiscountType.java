package store.buzzbook.coupon.common.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 할인 유형을 나타내는 enum 클래스입니다.
 * <p>
 * RATE 와 AMOUNT 의 두 가지 할인 유형이 있습니다.
 * 이 클래스는 Jackson 라이브러리와 통합되어 JSON 직렬화 및 역직렬화를 지원합니다.
 * </p>
 */
public enum DiscountType {
	RATE,
	AMOUNT;

	/**
	 * 문자열을 입력받아 해당하는 DiscountType enum 값을 반환합니다.
	 * <p>
	 * 입력된 문자열이 null 이거나 유효한 enum 값이 아닐 경우 IllegalArgumentException 을 발생시킵니다.
	 * </p>
	 *
	 * @param value 입력된 문자열
	 * @return 입력된 문자열에 해당하는 DiscountType 값
	 * @throws IllegalArgumentException 유효하지 않은 값이 입력된 경우
	 */
	@JsonCreator
	public static DiscountType fromString(String value) {
		if (value == null) {
			throw new IllegalArgumentException("enum 값을 찾을 수 없습니다.");
		}

		return switch (value.toLowerCase()) {
			case "rate" -> RATE;
			case "amount" -> AMOUNT;
			default -> throw new IllegalArgumentException("enum 값을 찾을 수 없습니다.");
		};
	}

	/**
	 * enum 값을 소문자 문자열로 반환합니다.
	 *
	 * @return 소문자로 변환된 enum 값
	 */
	@JsonValue
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
