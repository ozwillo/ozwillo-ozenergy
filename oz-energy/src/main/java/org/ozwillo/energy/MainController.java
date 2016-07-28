package org.ozwillo.energy;


import org.oasis_eu.spring.kernel.model.UserInfo;
import org.oasis_eu.spring.kernel.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {
	@Autowired
	private UserInfoService userInfoService;
	
	@ModelAttribute("user")
	public UserInfo user() {
		return userInfoService.currentUser(); // #186 NOT nameDefaults.complete(userInfo) which overrides ex. "en-GB fr" Kernel locale
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}


	@RequestMapping("/")
	public String index(Model model) {
		return "index";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		return "about";
	}
}
