package store.buzzbook.coupon.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;

import lombok.RequiredArgsConstructor;
import store.buzzbook.coupon.common.actuator.ApplicationStatus;

@RestController
@RequestMapping("/actuator/status")
@RequiredArgsConstructor
public class ApplicationStatusController {

	private final ApplicationInfoManager applicationInfoManager;
	private final ApplicationStatus applicationStatus;

	@PostMapping
	@ResponseStatus(value = HttpStatus.OK)
	public void stopStatus() {
		applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
		applicationStatus.stopService();
	}
}
