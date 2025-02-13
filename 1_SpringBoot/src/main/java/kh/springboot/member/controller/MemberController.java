package kh.springboot.member.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequiredArgsConstructor //를해서 맴버 컨트롤러에 대한 객체 생성
@SessionAttributes("loginUser")
@RequestMapping("/member/")
@Slf4j
public class MemberController {
	  
	
//	의존성 주입 1.  필드 주입 @Autowired
//	@Autowired
//	private MemberService mService;
	
	
//	의존성 주입 2. 생성자 주입 @RequiredArgsConstructor + final
//	@RequiredArgsConstructor : 특정변수(final이 붙은 상수 or @NonNull이 붙은 변수)만 생성자를 생성해주는 어노테이션(롬복에있음)
	private final MemberService mService;
	
	private final BCryptPasswordEncoder bcrypt;
	
//	private Logger log = LoggerFactory.getLogger(MemberController.class);
	
	
	private final JavaMailSender mailSender;
	
	
	// 로그인 화면 연결
	@GetMapping("signIn")
	public String signIn() {
		
		return "login" ;
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
//			required			기본 값 true, 지정한 파라미터가 꼭 필요한(필수적인) 변수인지 설정하는 속성

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
	
	@GetMapping("enroll")
	public String enroll() {
		//로그 레벨 : DEBUG <INFO < WARN < ERROR < FATAL
		//FATAL		:아주심각한에러
		//ERROR		:어던 요청 처리 중 문제 발생
		//WARN		:프로그램 실행에는 문제가 없지만 향후 시스템 에러의 원인이 될 수 있다는 경고성 메시지
		//INFO		:정보성 메시지
		//DEBUG		:디버깅 용도로 사용하는 메시지
		//TRACE		:디버그 레벨이 너무 광범위한 것을 해결하기 위해 좀더 상세한 이벤트를 나타냄
		
		//log.fatal("회원가입 페이지");
		log.error("회원가입 페이지");
		log.warn("회원가입 페이지");
		log.info("회원가입 페이지");
		log.debug("회원가입 페이지");
		log.trace("회원가입 페이지");
		return "enroll";
	}
	

	
	@PostMapping("enroll")
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
	@GetMapping("myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			String id = loginUser.getId();
			
			
			ArrayList<HashMap<String,Object>> list = mService.selectMyList(id);
//			System.out.println(list);
			mv.addObject("list",list);
			mv.setViewName("myInfo");			
		}
		
		return mv;
	}	
	
 
	// 암호화 후 로그인 + @SessionAttribute
	//		@SessionAttribute는 model에 attribute가 추가될때 자동으ㅗㄹ 키 값을 찾아 세션에 등록하는 어노테이션
	@PostMapping("signIn")
	public String login(Member m, Model model, @RequestParam("beforeURL") String beforeURL) {
		

		Member loginUser = mService.login(m);
		
		if(loginUser != null && bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
			model.addAttribute("loginUser",loginUser);
			if(loginUser.getIsAdmin().equals("Y")){
				return "redirect:/admin/home";
			}else {
//				log.debug(m.getId());
				return "redirect:" + beforeURL;
			}
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
	
	@GetMapping("logout")
	public String logout(SessionStatus session) {
		session.setComplete();
		return "redirect:/home";
	}
	
	
	
	
	//url로진행했기때문에 getmapping
	@GetMapping("edit")
	public String edit() {
		return "edit";
	}
	
	
	
	
	
	// 보내는애가 ~/edit임으로 post매핑
	
	@PostMapping("/edit")
	public String edit(@ModelAttribute Member m, @RequestParam("emailId") String emailId, 
						@RequestParam("emailDomain") String emailDomain, Model model) {
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId+"@"+emailDomain);
		}
		
		int result = mService.updateMember(m);
		if(result > 0) {
			model.addAttribute("loginUser",mService.login(m));
			return "redirect:/member/myInfo";
			
		}else {
			throw new MemberException("수정실패");
		}
	}
	
	
	
	
	
	
//	@PostMapping("/updatePassword")
//	public String updatePassword(@RequestParam("currentPwd") String currentPwd,
//								@RequestParam("newPwd") String newPwd,
//								@RequestParam("newPwdConfirm") String newPwdConfirm, Model model) {
//		
//		Member m = (Member)model.getAttribute("loginUser");
//		// login을 가져와야하기때문에 이렇게.
//		
//		if(!bcrypt.matches(currentPwd, m.getPwd()) || !newPwd.equals(newPwdConfirm)) {
//			throw new MemberException("비밀번호가 달라용 ㅋㅋ");
//		}
//		
//		m.setPwd(bcrypt.encode(newPwd));
//		
//		
//		int result = mService.updatePwd(m);
//		
//		
//		if(result > 0) {
//			model.addAttribute("loginUser",mService.login(m));
//			return "myInfo";
//		}else {
//			throw new MemberException("수정실패");
//		}
//	}
	

	@PostMapping("/updatePassword")
	public String updatePassword(@RequestParam("currentPwd") String pwd,
								@RequestParam("newPwd") String newPwd,
							 Model model) {
		
		Member m = (Member)model.getAttribute("loginUser");
		
		if(bcrypt.matches(pwd, m.getPwd())) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("id", m.getId());
			map.put("newPwd", bcrypt.encode(newPwd));
			int result = mService.updatePassword(map);
			if(result>0) {
				model.addAttribute("loginUser",mService.login(m));
				return "redirect:/home";
			}else {
				throw new MemberException("비밀번호 수정실패 ㅋㅋㅋ");
			}
				
		}else {
			throw new MemberException("비밀번호수정시랲 ㅋㅋ");
		}
	}
	


	@GetMapping("delete")
	public String delete(HttpSession session) {
		int result= mService.deleteMember(((Member)session.getAttribute("loginUser")).getId());
	
		if(result > 0) {
			return "redirect:/member/logout";
		}else {
			throw new MemberException("삭제  실패 ㅋㅋㅋ");
		}

	}
	
	
//	@GetMapping("checkId")
//	public void chieckId(@RequestParam("id") String id, PrintWriter out) {
//		int count = mService.checkId(id);
//		out.print(count);
//	}
//	
//	@GetMapping("checkNickName")
//	@ResponseBody
//	public String checkNickName(@RequestParam("nickName") String nickName) {
//		int count = mService.checkNickName(nickName);
//		return count == 0 ? "usable" : "unusable";
//	}
	
	@GetMapping("checkValue")
	@ResponseBody
	public int checkValue(@RequestParam("value") String value, @RequestParam("column") String column) {
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("value",value);
		map.put("column",column);
		int count = mService.checkValue(map);
		return count;
	}

	
	@PostMapping("profile")
	@ResponseBody
	public int updateProfile(@RequestParam(value="profile", required=false) MultipartFile profile,Model model) {
		Member m = (Member)model.getAttribute("loginUser");
		
		String savaPath = "c:\\profiles";
		File folder = new File(savaPath);
		
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		if(m.getProfile() != null) {
			File f = new File(savaPath + "\\" + m.getProfile());
			f.delete();
		}
		
		String renameFilename = null;
		if(profile!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			int ranNum = (int)(Math.random()*100000);
			String originalFilename = profile.getOriginalFilename();
			renameFilename = sdf.format(new Date()) + ranNum + originalFilename.substring(originalFilename.lastIndexOf("."));
			
			try {
				profile.transferTo(new File(folder + "\\" + renameFilename));
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
		
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("id",m.getId());
		map.put("profile", renameFilename);
		
		int result = mService.updateProfile(map);
		if(result > 0) {
			m.setProfile(renameFilename);
			model.addAttribute("loginUser",m);
		}
		
		return result;
	}
	
	
	@GetMapping("echeck")
	@ResponseBody
	public String checkEmail(@RequestParam("email") String email) {
		MimeMessage mimeMassage = mailSender.createMimeMessage();
		
		String subject = "[SpringBoot] 이메일 확인";
		String body = "<h1 align='center'>SpringBoot 이메일 확인</h1><br>";
		body += "<div style='border:3px solid green; text-align: center; font-size: 15px;'>본 메일은 이메일을 확인하기 위해 발송되었습니다.<br>";
		body += "아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br><br>";
		
		String random = "";
		for(int i=0; i<5; i++) {
			random += (int)(Math.random() * 10);
		}
		
		body += "<span style='font-size; 30px; text-decoration: underline;'><b>" + random + "</b></span><br></div>";
		
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMassage);
		
		try {
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(body,true);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		mailSender.send(mimeMassage);
		
		return random;
	}
	
	@GetMapping("findIDPW")
	public String findIDPW() {
		return "findIDPW";
	}
	
//	@PostMapping("fid")
//	public String findId(@ModelAttribute Member m, Model model) {
//		String id = mService.findId(m);
//		if(id != null) {
//			model.addAttribute("id",id);
//			return "findId";
//		}else {
//			throw new MemberException("존재하지않는 아이디입니다다");
//		}
//		
//	}
//	
//	@PostMapping("fpw")
//	public String findPw(@ModelAttribute Member m) {
//		Member member = mService.findPw(m);
//		if(member !=null) {
//			return "resetPw";
//		}else {
//			throw new MemberException("존재하지않는 회원입니다");
//		}
//		
//	}
	
	//위에 두개를 합치자.
	@PostMapping("fInfo")
	public String findId(@ModelAttribute Member m, Model model) {
		Member member = mService.findInfo(m);
		if(member != null) {
			model.addAttribute("id",member.getId()); // findId.html에 보내는 용도
			return m.getName() != null ? "findId" : "resetPw";
		}else {
			throw new MemberException("존재하지않는 아이디입니다다");
		}
		
	}
	
	@PostMapping("fpwUpdate")
	public String updatePwd(@ModelAttribute Member m, Model model) {
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("id", m.getId());
		map.put("newPwd", bcrypt.encode(m.getPwd()));
		int result = mService.updatePassword(map);  	// updatePwd재활용
		
		if(result>0) {
			model.addAttribute("msg", "비밀번호 수정이 완료되었습니다.");
			model.addAttribute("url", "/home");
			return "views/common/sendRedirect";
		}else {
			throw new MemberException("비밀번호 수정실패.");
		}
	}
	
	
	
	
}

