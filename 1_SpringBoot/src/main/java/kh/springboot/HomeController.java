package kh.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller //컨트롤러 역할을 하는 bean 생성 어노테이션
public class HomeController {

	@GetMapping("home")
	public String home() {
		return "views/home";//classpath:templates/views/home.html > 아하 forward방식이구나!
	}
	
	
	
	
	
	
	
}
