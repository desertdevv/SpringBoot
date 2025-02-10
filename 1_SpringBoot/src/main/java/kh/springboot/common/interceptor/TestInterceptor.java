package kh.springboot.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestInterceptor implements HandlerInterceptor{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		System.out.println("================= START =================");
		System.out.println(request.getRequestURI() + "\n");
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
//		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//		리턴값이 없기떄문에 지울거다
		System.out.println("================= VIEW =================");
		System.out.println(request.getRequestURI());
		if(modelAndView != null) {
			System.out.println(modelAndView.getModel());
			System.out.println(modelAndView.getViewName()+"\n");
		}

	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
//		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
//		여기도 마찬가지로 리턴값이 없어서 지운다
		System.out.println("================= END =================");
		System.out.println(request.getRequestURI() + "\n");


	}
}
