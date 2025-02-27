package kh.springboot.ajax.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;

@RestController //@controller + @responsebody
@RequestMapping({"/member","/board","/admin"})
@RequiredArgsConstructor
@SessionAttributes("loginUser")
public class AjaxController {
	
	private final MemberService mService;
	private final JavaMailSender mailSender;
	private final BoardService bService;
	
	@GetMapping("checkValue")
	public int checkValue(@RequestParam("value") String value, @RequestParam("column") String column) {
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("value",value);
		map.put("column",column);
		int count = mService.checkValue(map);
		return count;
	}
	
	
	
	
	
	@PutMapping("profile")
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
	
	
	@PostMapping("list")
	public int insertTodo(@ModelAttribute TodoList todo) {
		return mService.insertTodo(todo);
	}
	
	
	@PutMapping("list")
	public int updateTodo(@ModelAttribute TodoList todo) {
		return mService.updateTodo(todo);
	}
	
	@DeleteMapping("list")
	public int deleteToto(@RequestParam("num") int num) {
		return mService.deleteTodo(num);
	}
	
	@GetMapping("top") 
	public ArrayList<Board> selectTop(HttpServletResponse response) {
		//HttpMassageConverter
		//기본문자 : StringHttpMassageConverter
		//기본객체 : MAppingJackson2HttpMassageConverter  > application/json 방식으로바꿔서전달해준다
		
//		response.setContentType("application/json; charset=UTF-8");
//		
//		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
//		Gson gson = gb.create();		
//		
//		try {
//			gson.toJson(list, response.getWriter());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		ArrayList<Board> list = bService.selectTop();
		//위를 안써주고 그냥 return list를해줄수있다
		return list;
	}
	
	
	@PostMapping(value="reply")
	public ArrayList<Reply> insertReply(@ModelAttribute Reply r) {
		int result = bService.insertReply(r);
		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
		
//		ObjectMapper om = new ObjectMapper();
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		om.setDateFormat(sdf);
//		//잭슨은 그냥하면 밀리세컨을 가져온다 그래서 위의설정.
//		
//		String strJson = null;
//		try {
//			strJson = om.writeValueAsString(list);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//		
//		return strJson;
		//여기도 지워준다
		return list;
	}
	
	
	@DeleteMapping("reply")
	public int deleteReply(@RequestParam("rId") int rId){
		return bService.deleteReply(rId);

		
	}
	
	
	@PutMapping("reply")
	public int updateReply(@ModelAttribute Reply r) {
		return bService.updateReply(r);
	}
	
	@PutMapping("members")
	public int updateMember(@RequestParam("val") String value, @RequestParam("col") String column, @RequestParam("id") String id) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("val", value);
		map.put("col", column);
		map.put("id", id);
		return mService.updateMemberItem(map);
	}
	
	
}
