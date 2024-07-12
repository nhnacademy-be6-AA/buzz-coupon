package store.buzzbook.coupon.common.utils;

import java.security.SecureRandom;

/**
 * 고유한 코드 생성을 위한 유틸리티 클래스입니다.
 * <p>
 * 이 클래스는 임의의 문자와 숫자로 구성된 코드를 생성하는 메서드를 제공합니다.
 * 코드 생성 형식은 그룹 길이 4와 그룹 개수 4을 가집니다.
 * </p>
 */
public class CodeCreator {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final int GROUP_LENGTH = 5;
	private static final int GROUP_COUNT = 4;
	private static final SecureRandom random = new SecureRandom();

	/**
	 * 이 클래스는 인스턴스화할 수 없습니다.
	 * <p>
	 * 유틸리티 클래스이므로, 인스턴스화하려고 하면 IllegalStateException 이 발생합니다.
	 * </p>
	 */
	private CodeCreator() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 고유한 코드를 생성합니다.
	 * <p>
	 * 생성된 코드는 대문자 알파벳과 숫자로 구성됩니다.
	 * 예: "ABCDEFGHIJKLNOPQ123345"
	 * </p>
	 *
	 * @return 생성된 고유 코드 문자열
	 */
	public static String createCode() {
		StringBuilder serial = new StringBuilder(GROUP_LENGTH * GROUP_COUNT);
		for (int i = 0; i < GROUP_COUNT; i++) {
			for (int j = 0; j < GROUP_LENGTH; j++) {
				int index = random.nextInt(CHARACTERS.length());
				serial.append(CHARACTERS.charAt(index));
			}
		}
		return serial.toString();
	}
}
