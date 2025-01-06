package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration //  설정파일 클래스를 빈으로 등록
public class SecurityConfig {// 설정파일의 역할을 하는 클래스
	
	@Bean
	public BCryptPasswordEncoder getPasswordEncoding() {
		return new BCryptPasswordEncoder();
	}
}
