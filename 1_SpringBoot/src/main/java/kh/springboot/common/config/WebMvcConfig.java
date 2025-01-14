package kh.springboot.common.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")	// 매핑할 uri를 설정 (ex. 파일을 가지고 ㅇ로때 경로를 어떻게 쓸지 결정)
				.addResourceLocations("file:///d:/uploadFiles/", "classpath:/static/"); // 정적 리소스 위치
	}
}
