package kh.springboot.member.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor//를해서 맴버 컨트롤러에 대한 객체 생성
@SessionAttributes("loginUser")
public class MemberController {
	
	
//	의존성 주입 1.  필드 주입 @Autowired
//	@Autowired
//	private MemberService mService;
	
	
//	의존성 주입 2. 생성자 주입 @RequiredArgsConstructor + final
//	@RequiredArgsConstructor : 특정변수(final이 붙은 상수 or @NonNull이 붙은 변수)만 생성자를 생성해주는 어노테이션(롬복에있음)
	private final MemberService mService;
	
	private final BCryptPasswordEncoder bcrypt;
	
	
	
	
	
	// 로그인 화면 연결
	@GetMapping("/member/signIn")
	public String signIn() {
		
		return "views/member/login" ;
	}
	
	/********* 파라미터 전송방법*********/
	//로그인
	
//	방법 1. (Servlet)방식 HttpServletRequset 이용
//	
//
//	@PostMapping("/member/signIn")
//	public void login(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		String pwd = request.getParameter("pwd");
//		System.out.println("id1 : " + id);
//		System.out.println("pwd1 : " + pwd);
//	}
	
//	방법 2 @RequestParam 이용
//			value				view에서 받아올 파라미터 이름(view의 name)이 들어가는 곳
//								@RequestParam에 들어가는 속성이 value만 있다면 생략가능
//			defaultvalue		값이 null이거나 들어오지 않았을 때 기복적으로 들어갈 데이터를 지정하는 속성
//			required			기본 값 true, 지정한 파라미터가 꼭 필요한(필수적인) 변수인지 설저앟는 속성

//	@PostMapping("/member/signIn")
////	public void login(@RequestParam("id") String userId, @RequestParam("pwd") String userPwd) {
//
////	public void login(@RequestParam(value="id", defaultValue="testId") String userId, @RequestParam("pwd") String userPwd) {
//	public void login(@RequestParam(value="id", defaultValue="testId") String userId, 
////			@RequestParam("pwd") String userPwd, @RequestParam("tt") String t) {
////			이러면 400에러가 난다(없는 파라미터를 받아오려하기때문
////			@RequestParam("pwd") String userPwd, @RequestParam(value="tt", required=false) String t) {
//			@RequestParam("pwd") String userPwd, @RequestParam(value="tt", defaultValue="tt") String t) {
//// 			이렇게 도 되는데 사용자한테 받는값이니까 사용자가 진짜 입력을 안했는지 입력을 했는지 알수없는 단점이있다.
//
//
//
//		System.out.println("id2 : " + userId);
//		System.out.println("pwd2 : " + userPwd);
//		System.out.println("tt : " + t);
//	
//
//	}
//	
	
	
//	3. @RequestParam 생략
//	내가 지금 쓸 변수 명과 파라미터 명을 일치시켜주면 알아서 매핑시켜준다
//	근데 가능했었었고 버전업시켜주면서 안된다
//	단점 : 파라미터를 받아오는지 변수를 쓰는지 알수없다
//	@PostMapping("/member/signIn")
//	public void login(String id, String pwd) {
//		
//		System.out.println("id3 : " + id);
//		System.out.println("pwd3 : " + pwd);
//
//	}
	
	
	
//	4. @ModelAttribute 이용
//	기본 생성자와 setter를 이용한 주입방식 이기때문에 둘다있어야한다.
//	파라미터명과 세터명이 같아야한다
//	
//	@PostMapping("/member/signIn")
//	public void login(@ModelAttribute Member m) {
//		
//		System.out.println("id4 : " + m.getId());
//		System.out.println("pwd4 : " + m.getPwd());
//
//	}	
	
	
	
//	5. @ModelAttribute 생략
	
//	@PostMapping("/member/signIn")
//	public String login(Member m,HttpSession session) {
//		
//
//		Member loginUser = mService.login(m);
//		
//		if(loginUser != null) {
//			session.setAttribute("loginUser",loginUser);
//			return "redirect:/home";
//			
//		}else {
//			throw new MemberException(
//					 "\r\n"
//					+ "⣿⣿⣿⣿⠿⠿⠿⢿⡿⠿⠿⠿⢿⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⡇ ⣤⣤⣤⡇⠀⣤⣤⣤⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣇ ⠉⠉⠉⡇⠀⠉⠉⠉⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⠿⠿⠿⠿⠀ ⠿ ⠿⠿⠿⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣤⣤⣤⠤⠤⠤⠤⢤⣤⣤⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣿⠉⠀⣤⣤⣤⣤⡀⠈⢻⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣿⣄⡀⠉⠙⠛⠉⠁⣠⣾⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿");
//			//예외 강제 발생 시키기
//		}
//	}
	
	@GetMapping("/member/enroll")
	public String enroll() {
		return "views/member/enroll";
	}
	

	
	@PostMapping("member/enroll")
	public String enroll(@ModelAttribute Member m, @RequestParam("emailId") String emailId, @RequestParam("emailDomain") String emailDomain) {
		
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@"+ emailDomain);
		}
		System.out.println(m); //여기서는 비밀번호가 보이고
		System.out.println(bcrypt);
	
		m.setPwd(bcrypt.encode(m.getPwd()));
		System.out.println(m); // 이설정을하면 비밀번호가 다른걸로 바뀐다
		
		
		int result = mService.insertMember(m);
		if(result >0) {
			return "redirect:/home";
		}else {
			throw new MemberException("회원가입을 실패하였습니다.");
		}
	
	}
	
	
	
	// 암호화 후 로그인 
	
//	@PostMapping("/member/signIn")
//	public String login(Member m,HttpSession session) {
//		
//
//		Member loginUser = mService.login(m);
//		
//		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//			session.setAttribute("loginUser",loginUser);
//			return "redirect:/home";
//			
//		}else {
//			throw new MemberException(
//					 "\r\n"
//					+ "⣿⣿⣿⣿⠿⠿⠿⢿⡿⠿⠿⠿⢿⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⡇ ⣤⣤⣤⡇⠀⣤⣤⣤⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣇ ⠉⠉⠉⡇⠀⠉⠉⠉⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⠿⠿⠿⠿⠀ ⠿ ⠿⠿⠿⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣤⣤⣤⠤⠤⠤⠤⢤⣤⣤⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣿⠉⠀⣤⣤⣤⣤⡀⠈⢻⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣿⣄⡀⠉⠙⠛⠉⠁⣠⣾⣿⣿⣿\r\n"
//					+ "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿");
//			//예외 강제 발생 시키기
//		}
//	}
	
	/******* 요청후 전달하고자 있는 데이터가 있는 경우 *******/
//	1.model이용
//		맵형식(key,value)으로 request scope에 담아 전달
		
	
	
	//내정보 조회
//	@GetMapping("/member/myInfo")
//	public String myInfo(HttpSession session, Model model) {
//		Member loginUser = (Member)session.getAttribute("loginUser");
//		if(loginUser != null) {
//			String id = loginUser.getId();
//			
//			
//			ArrayList<HashMap<String,Object>> list = mService.selectMyList(id);
////			System.out.println(list);
//			model.addAttribute("list",list);
//			
//		}
//		
//		return "views/member/myInfo";
//	}
//	
//	
	
	
	//2. ModelAndview 이용
//	 model + view
	@GetMapping("/member/myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			String id = loginUser.getId();
			
			
			ArrayList<HashMap<String,Object>> list = mService.selectMyList(id);
//			System.out.println(list);
			mv.addObject("list",list);
			mv.setViewName("views/member/myInfo");			
		}
		
		return mv;
	}	
	

	// 암호화 후 로그인 + @SessionAttribute
	//		@SessionAttribute는 model에 attribute가 추가될때 자동으ㅗㄹ 키 값을 찾아 세션에 등록하는 어노테이션
	@PostMapping("/member/signIn")
	public String login(Member m, Model model) {
		

		Member loginUser = mService.login(m);
		
		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
			model.addAttribute("loginUser",loginUser);
			return "redirect:/home";
			
		}else {
			throw new MemberException(
					 "\r\n"
					+ "⣿⣿⣿⣿⠿⠿⠿⢿⡿⠿⠿⠿⢿⣿⣿⣿\r\n"
					+ "⣿⣿⣿⡇ ⣤⣤⣤⡇⠀⣤⣤⣤⣿⣿⣿\r\n"
					+ "⣿⣿⣿⣇ ⠉⠉⠉⡇⠀⠉⠉⠉⣿⣿⣿\r\n"
					+ "⣿⣿⣿⠿⠿⠿⠿⠀ ⠿ ⠿⠿⠿⣿⣿⣿\r\n"
					+ "⣿⣿⣿⣤⣤⣤⠤⠤⠤⠤⢤⣤⣤⣿⣿⣿\r\n"
					+ "⣿⣿⣿⣿⠉⠀⣤⣤⣤⣤⡀⠈⢻⣿⣿⣿\r\n"
					+ "⣿⣿⣿⣿⣄⡀⠉⠙⠛⠉⠁⣠⣾⣿⣿⣿\r\n"
					+ "⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿");
			//예외 강제 발생 시키기
		}
	}	
	
	
	
	
	//@SessionAttribute 추가후 로그아웃
	
	@GetMapping("/member/logout")
	public String logout(SessionStatus session) {
		session.setComplete();
		return "redirect:/home";
	}
	
	
}
