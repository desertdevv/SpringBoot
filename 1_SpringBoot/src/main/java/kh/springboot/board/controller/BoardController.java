package kh.springboot.board.controller;

import java.awt.BorderLayout;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	
	
//	// http://board/17/1 이런식으로 주소가 계속바뀌는데 어떻게 해야할까
//	// post방식에서 데이터를 보낼때 많이사용한다
//	@GetMapping("/{id}/{page}")
//	public String selectBoard(@PathVariable("id") int bId, @PathVariable("page") int page, Model model) {
//		
//		//글 상세 조회 + 조회수 수정 
//		//				내가 내글을 조회 or 비회원이 조회 > 조회수 갱신 x
//		//				현재 로그인한 사람의 아이디 필요
//		
//		// bId, id를 서비스에 넘겨서 글쓴이 비교 로직 작성 (컨트롤러에서 작성하지말고 서비스에서 작성하시오)
//		
//		// 게시글이 존재하면, 게시글 데이터(b)와 페이지(page)를 /views/board/detail.html로 전달
//		//													detail은 어제썼던 write를 수정해서 사용할것임.
//		
//		Member m = (Member)model.getAttribute("loginUser");
//		Board b = bService.selectBoard(m, bId);
//		b.setBoardType(1);
//		
//		if(b!=null) {
//			model.addAttribute("b",b);
//			model.addAttribute("page",page);
//			return "detail";
//		}else {
//			throw new BoardException("글 선택 실패");
//		}
//		
//	}
//	
	
	
	@GetMapping("/{id}/{page}")
	public ModelAndView selectBoard(@PathVariable("id") int bId, @PathVariable("page") int page, HttpSession session, ModelAndView mv) {
		
		
//		//글 상세 조회 + 조회수 수정 
//		//				내가 내글을 조회 or 비회원이 조회 > 조회수 갱신 x
//		//				현재 로그인한 사람의 아이디 필요
		Member loginUser = (Member)session.getAttribute("loginUser");
		// 뒤에 바로 getId를 안받는 이유 : 비회원이면 null이니까 못받음으로.
		String id = null;
		if(loginUser != null) {
			id = loginUser.getId();
		}
		 
//		bid, id를 service에 넘겨서 글쓴이 비교 로직 작성
		Board b = bService.selectBoard(bId,id);
		
		
//		// 게시글이 존재하면, 게시글 데이터(b)와 페이지(page)를 /views/board/detail.html로 전달
//		//													detail은 어제썼던 write를 수정해서 사용할것임.
//		게시글이 존재하지않으면 사용자 정의 예외발생
		
		if(b!=null) {
			mv.addObject("b",b).addObject("page",page).setViewName("detail");
			return mv;
		}else {
			throw new BoardException("게시글 상세 조회를 실패하였습니다.");
		}
	}
	
	
	
	
	
	
	
	@PostMapping("updForm")
	public String updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		model.addAttribute("b",b).addAttribute("page",page);
		
		

		return "views/board/edit";
		//그냥 edit이라고 하면 member의edit으로 찾는다

		
	}
	
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page) {
		b.setBoardType(1);
		int result = bService.updateBoard(b);
		if(result>0) {
//			return "redirect:/board/"+ b.getBoardId()+ "/" +page;
			return String.format("redirect:/board/%d/%d", b.getBoardId(),page);
		}else {
			throw new BoardException("수정불가");
		}
	}
	
	
	@PostMapping("delete")
	public String deleteBoard(@RequestParam("boardId") int bId, HttpServletRequest request) {
		int result = bService.deleteBoard(bId);
		if(result>0) {
//			return "redirect:/" + (request.getRequestURI().contains("board") ? "board" : "attm" ) + "/list";
			return "redirect:/" + (request.getHeader("referer").contains("board") ? "board" : "attm" ) + "/list";
		}else {
			throw new BoardException("넌 못지나간다");		}
	}
	
	
}
