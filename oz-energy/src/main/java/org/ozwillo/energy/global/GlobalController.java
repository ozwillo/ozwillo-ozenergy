package org.ozwillo.energy.global;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class GlobalController {

	@RequestMapping("/global")
	public String index(Model model) {
		return "/global/global";
	}
}
