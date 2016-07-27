package org.ozwillo.energy.my;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MyController {

	@RequestMapping("/my")
	public String index(Model model) {
		
		return "my/conso";
	}
}
