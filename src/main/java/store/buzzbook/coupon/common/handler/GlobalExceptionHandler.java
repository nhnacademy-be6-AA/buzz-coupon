package store.buzzbook.coupon.common.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import store.buzzbook.coupon.common.exception.CouponAlreadyExistsException;
import store.buzzbook.coupon.common.exception.CouponNotFoundException;
import store.buzzbook.coupon.common.exception.CouponPolicyNotFoundException;
import store.buzzbook.coupon.common.exception.CouponTypeNotFoundException;

/**
 * 전역 예외 처리를 담당하는 클래스입니다.
 * <p>
 * 다양한 예외 상황에 대해 적절한 HTTP 응답을 반환합니다.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * IllegalArgumentException 이 발생했을 때 HTTP 400 상태 코드와 함께 예외 메시지를 반환합니다.
	 *
	 * @param exception 처리할 예외
	 * @return HTTP 400 상태 코드와 예외 메시지를 포함한 ResponseEntity
	 */
	@ExceptionHandler({IllegalArgumentException.class})
	public ResponseEntity<String> handleBadRequest(Exception exception) {
		log.debug("handleBadRequest : {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}

	/**
	 * 쿠폰 관련 예외(CouponNotFoundException, CouponPolicyNotFoundException, CouponTypeNotFoundException)가 발생했을 때
	 * HTTP 404 상태 코드와 함께 예외 메시지를 반환합니다.
	 *
	 * @param exception 처리할 예외
	 * @return HTTP 404 상태 코드와 예외 메시지를 포함한 ResponseEntity
	 */
	@ExceptionHandler({CouponNotFoundException.class, CouponPolicyNotFoundException.class,
		CouponTypeNotFoundException.class})
	public ResponseEntity<String> handleNotFound(Exception exception) {
		log.debug("handleNotFound : {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
	}

	/**
	 * CouponAlreadyExistsException 이 발생했을 때 HTTP 409 상태 코드와 함께 예외 메시지를 반환합니다.
	 *
	 * @param exception 처리할 예외
	 * @return HTTP 409 상태 코드와 예외 메시지를 포함한 ResponseEntity
	 */
	@ExceptionHandler({CouponAlreadyExistsException.class})
	public ResponseEntity<String> handleAlreadyExists(Exception exception) {
		log.debug("handleAlreadyExists : {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
	}

	/**
	 * 메서드 인자 유효성 검사 실패 시 HTTP 400 상태 코드와 함께 오류 메시지를 반환합니다.
	 *
	 * @param ex 유효성 검사 예외
	 * @param headers HTTP 헤더
	 * @param status HTTP 상태 코드
	 * @param request 웹 요청
	 * @return HTTP 400 상태 코드와 오류 메시지를 포함한 ResponseEntity
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((org.springframework.validation.FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		log.debug("handleMethodArgumentNotValid : {}", errors);
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
}
