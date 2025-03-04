package kh.springboot.admin.controller;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final BoardService bService;
	private final MemberService mService;
	
	@GetMapping("home")
	public String meoveToMaininAdmin(Model model) {
		File f = new File("D:/logs/member/");
		File[] files = f.listFiles();
		
		TreeMap<String, Integer> dateCount = new TreeMap<String, Integer>();
		BufferedReader br = null;
		try {
			for(File file:files) {
//			System.out.println(file);
				br = new BufferedReader(new FileReader(file));
				String data;
				while((data = br.readLine()) !=null){
//					System.out.println(data);
					String date = data.split(" ")[0];
					if(dateCount.containsKey(date)) {
						dateCount.put(date, dateCount.get(date) + 1);
					} else {
					dateCount.put(date,1);
					}
				}
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<Board> list = bService.selectRecentBoards();
		
		model.addAttribute("dateCount",dateCount);
		model.addAttribute("list",list);
		System.out.println(list.toString());
		
		return "admin";
	}
	
	
	
	@GetMapping("members")
	public String selectMembers(Model model) {
		ArrayList<Member> list = mService.selectMembers();
		model.addAttribute("list",list);
		return "members" ;
	}
	
	@GetMapping("boards")
	public String selectBoards(@RequestParam(value="page", defaultValue="1") int currentPage, Model model, HttpServletRequest request) {
		//쿼리 예상 : 게시글에대한 갯수가져오기(getListCount이용)
		/**
		 * select count(*)
		 * from board
		 * where board_type = 1
		 */
		int listCount = bService.getListCount(-1);
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 10);
		ArrayList<Board> list = bService.selectBoardList(pi, -1);
		
		model.addAttribute("list",list);
		model.addAttribute("pi",pi);
		model.addAttribute("loc",request.getRequestURL());
		
		return "boards";
	}
	

	
	
	
}
