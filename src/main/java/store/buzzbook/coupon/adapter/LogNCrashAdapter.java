package store.buzzbook.coupon.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import store.buzzbook.coupon.dto.log.LogNCrashRequest;

@FeignClient(name = "LogNCrashAdapter", url = "${logncrash.url}")
public interface LogNCrashAdapter {

	@PostMapping("/v2/log")
	void sendLog(@RequestBody LogNCrashRequest request);
}
