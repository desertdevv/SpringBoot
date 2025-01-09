package kh.springboot.board.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")




public class BoardController {
	private final BoardService bService;
	
	
	
	@GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue="1") int currentPage, 
									Model model, HttpServletRequest request) {
		
		int listCount = bService.getListCount(1);
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		ArrayList<Board> list = bService.selectBoardList(pi, 1);
		
		model.addAttribute("list",list).addAttribute("pi",pi);
		model.addAttribute("loc", request.getRequestURI());
		// getRequestURI() : /board/list
		// getRequestURL() : http://localhost:8080/board/list
		
		return "list";
		
	}
	
	
//	글쓰기누르면 write로갈수있게
	@GetMapping("write")
	public String write() {
		return "write";
	}
	
	
	
	
//	//insert페이지에관한설정
//	@PostMapping("insert")
//	public String insertBoard(Board board, HttpSession session) {
//		String id = ((Member)session.getAttribute("loginUser")).getId();
//		
//		board.setBoardWriter(id);
//		
//		int result = bService.insertBoard(board);
//		
//		if(result>0) {
//			return "redirect:/board/list";
//		}else {
//			throw new BoardException("글만들기 실패");
//		}
//		
//		
//		
//	}
//	
	
	
	
	//insert페이지에관한설정
	@PostMapping("insert")
	public String insertBoard(@ModelAttribute Board b, HttpSession session) {
//		model을 못쓰는이유 modeld으ㅜㄹ sesscionattribuye에 저장한적이없기때문에
		b.setBoardWriter(((Member)session.getAttribute("loginUser")).getId());
		b.setBoardType(1);
		
		int result = bService.insertBoard(b);
		if(result>0) {
			return "redirect:/board/list";
		}else {
			throw new BoardException("글만들기 실패");
		}		
		
		  
		
	}
	
	
	
	
	
	
	

}
