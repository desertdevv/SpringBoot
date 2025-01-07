package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
//새로운 빈을 넣을거니까 사용
public class TemplateResolverConfig {
	
	@Bean
	public ClassLoaderTemplateResolver memberResolver() {
		ClassLoaderTemplateResolver mResolver = new ClassLoaderTemplateResolver();
		mResolver.setPrefix("templates/views/member/");
		//원래는 "templates/" 가 기본경로인데 여기에 views를 추가.
		mResolver.setSuffix(".html");
		mResolver.setTemplateMode(TemplateMode.HTML);
		mResolver.setCharacterEncoding("UTF-8");
		mResolver.setCacheable(false);
		//서버껏다켯다
		mResolver.setCheckExistence(true);
		// 제일 중요한 설정. 얘를 넣어주면 자동으로 돌아가면서 찾아준다. 그래서 밖에있는 home도 위치를 찾아준다.
		return mResolver;
	}
	
	

}
