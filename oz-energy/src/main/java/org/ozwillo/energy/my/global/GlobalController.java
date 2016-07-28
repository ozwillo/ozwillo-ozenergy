package org.ozwillo.energy.my.global;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class GlobalController {

	@RequestMapping("/my/global")
	public String index(Model model) {
		return "/my/global";
	}
}
