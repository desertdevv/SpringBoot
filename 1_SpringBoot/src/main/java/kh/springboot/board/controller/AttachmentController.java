package kh.springboot.board.controller;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attm/")
@RequiredArgsConstructor
//의존성 주입중 이거 이용 > 밑에 final
public class AttachmentController {
	private final BoardService bService;
	
	@GetMapping("list")
	public String selectList(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model, HttpServletRequest request) {
		int listCount = bService.getListCount(2);
		//첨부파일 게시판이여서 2로 가져오자.
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9);
		//9의 게시물을 가져올거여서 9
		
		ArrayList<Board> bList = bService.selectBoardList(pi, 2);
		
		ArrayList<Attachment> aList = bService.selectAttmBoardList(null);
		// 썸네일 사진만 가져올거임 . (level이 0인것만가져오자)
		
		
		if(bList != null) {
			model.addAttribute("bList", bList).addAttribute("aList",aList).addAttribute("pi",pi).addAttribute("loc", request.getRequestURI());
			return "views/attm/list";
			// 이렇게 경로를 다적는 이유는 리졸버에서 member에 이름 같은것들에 다 걸리기 때문이다.
		} else {
			throw new BoardException("첨부파일 게시글 조회를 실피했습니다.");
		}
	}
	
	
	
	@GetMapping("write")
	public String writeAttm() {
		return "views/attm/write";
	}
	
	
	@PostMapping("insert")
	@Transactional
	// 에러나면 롤백해주는 어노테이션
	public String insertAttmBoard(@ModelAttribute Board b, @RequestParam("file") ArrayList<MultipartFile> files, HttpSession session) {
		//request.parameterValues 로 썼었다.
		System.out.println("초기 b " + b);
		System.out.println(files);
		
		String id = ((Member)session.getAttribute("loginUser")).getId();
		b.setBoardWriter(id);
		//id가 없으니까 로그인한 회원의 아이디를 집어넣어주는 작업.
		
		ArrayList<Attachment> list = new ArrayList<Attachment>(); // view에서 넘긴 파일 정보를 가지고 있는 list
		
		for(int i =0; i<files.size(); i++) {
			MultipartFile upload = files.get(i);
//			System.out.println(upload.getOriginalFilename());
			if(!upload.getOriginalFilename().equals("")) {
//			isEmpty가 있지만 안쓰는이유 : 파일만만들고 내용을 안쓰면 0이라서 걸러져서 안쓴다
				String[] returnArr = saveFile(upload); //첨부파일 폴더에 파일 저장
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					
					list.add(a);
				}
			}	
		}
		//retunrArr[] 은 밑에 정의 되어있다.
		
		
		
		//레벨에대해서 집어넣자 ( 섬네일)
		
		for(int i = 0 ; i<list.size(); i++) {
			Attachment a = list.get(i);
			if(i==0) {
				a.setAttmLevel(0);
			}else {
				a.setAttmLevel(1);
			}
		}
		
		System.out.println(list);
		
		int result1=0;
		int result2=0;
		
		if(list.isEmpty()) {
			b.setBoardType(1);
			result1 =bService.insertBoard(b);
		}else{
			b.setBoardType(2);
			result1 = bService.insertBoard(b);
			System.out.println("insert 후 b : " + b);
			
			for(Attachment a : list) {
				a.setRefBoardId(b.getBoardId());
			}
//			시퀀스가 아닌 boardId값으로 변경
			
			
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() + 1) {
			if(result2 == 0) {
				return "redirect:/board/list";
			}else {
				return "redirect:/attm/list";
			}
		}else {
			for(Attachment a : list) {
				deleteFile(a.getRenameName());
			}
			throw new BoardException("첨부파일 게시글 작성을 실패하였습니다.");
		}
		
	}
	
	public String[] saveFile(MultipartFile upload) {
		String savePath = "d:\\uploadFiles";
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		// folder.mkdirs() : 경로에 해당 폴더가 없으면 폴더를 새로 생성해주는 멧소드
		
		
		
	
	
		//같은 폴더에 같은 이름의 파일이 저정되지 않도록 rename > 년월일시분초밀리랜덤수.확장자
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100000);
		String originFileName = upload.getOriginalFilename();
		String renameFileName = sdf.format(new Date()) + ranNum + originFileName.substring(originFileName.lastIndexOf("."));
		//왜 lastindexof 를쓰냐면 apple.png인 경우도있지만 a.pp.le.png처럼 있을수도있으니까 마지막에있는 .을 찾으려고.
		
		//	System.out.println(renameFileName);
		
		String renamePath = folder + "\\" + renameFileName;
		try {
			upload.transferTo(new File(renamePath));
		} catch (IllegalStateException | IOException e) {
			System.out.println("파일 전송 에러 : " + e.getMessage());
		}
		
		String[] returnArr = new String[2];
		returnArr[0] = savePath;
		returnArr[1] = renameFileName;
		
		return returnArr;
		
		
		
		
		
	
	}
	
	
	public void deleteFile(String renameName) {
		String savePath = "d:\\uploadFiles";
		
		File f = new File(savePath + "\\" + renameName);
		if(f.exists()) {
			f.delete();
		}
	}
	
	
	@GetMapping("/{id}/{page}")
	public ModelAndView selectAttm(@PathVariable("id") int bId, @PathVariable("page") int page, HttpSession session, ModelAndView mv){
		Member loginUser = (Member)session.getAttribute("loginUser");
		String id = null;
		if(loginUser != null) {
			id = loginUser.getId();
		}
		Board b = bService.selectBoard(bId, id);
		
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		/*
		 * select *
		 * from attachment
		 * where attm_status = 'Y' and ref_board_id = #{bId}
		 */
		
		if(b != null) {
			mv.addObject("b",b).addObject("page",page).addObject("list",list).setViewName("views/attm/detail");
			return mv;
		}else {
			throw new BoardException("첨부파일 게시글 상세보기를 실패하였습니다.");
		}
	}
	
	
	@PostMapping("updForm")
	public String updateForm(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		
		model.addAttribute("b",b).addAttribute("list",list).addAttribute("page",page);
		return "views/attm/edit";
	}
	
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page,
								@RequestParam("deleteAttm") String[] deleteAttm, @RequestParam("file") ArrayList<MultipartFile> files) {
		
		
		System.out.println(b);
		System.out.println(Arrays.toString(deleteAttm));
		for(MultipartFile mf : files) {
			System.out.println("fileName : " + mf.getOriginalFilename());
		}
		
		/*
		  1. 새 파일 o
		 	1) 기존 파일 모두 삭제 > 기존파일 모두삭제 & 새 파일 저장
		 						새 파일 중에서 level 0, 1 지정해줘야한다
Board(boardId=30, boardTitle=마술하나 보여줄까 ?, boardWriter=null, nickName=null, boardContent=우 후후후, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
[202501141046333954743.jpg/0, 2025011410463339934295.jpg/1]
fileName : 태혁군.jpg			
		 						
		 	2) 기존 파일 일부 삭제 > 기존파일 일부삭제 & 새파일저장
		 						삭제할 파일의 level을 먼저 검사 후
		 						level이 0인 파일이 삭제되면 다른 기존파일의 레벨을 0으로 재지정
		 						새파일은 모두 1로 지정
Board(boardId=30, boardTitle=마술하나 보여줄까 ?, boardWriter=null, nickName=null, boardContent=우 후후후, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
[202501141046333954743.jpg/0, ]
fileName : 태혁군.jpg
fileName :					
		 						
		 	3) 기존 파일 모두 유지 > 새 파일 저장
		 						새 파일의 레벨은 모두 1로 저장
Board(boardId=30, boardTitle=마술하나 보여줄까 ?, boardWriter=null, nickName=null, boardContent=우 후후후, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
[, ] :2 
or
[] : 0 기존 첨부파일은 1개인 상황에서 삭제를 안하겠다.
fileName : 태혁군.jpg
fileName : 		 						
		 						
		  2. 새 파일 x
		 	1) 기존 파일 모두 삭제 > 기존 파일 모두 삭제
		 						일반 게시판으로 이동 : board_type = 1
Board(boardId=30, boardTitle=마술하나 보여줄까 ?, boardWriter=null, nickName=null, boardContent=우 후후후, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
[202501141046333954743.jpg/0, 2025011410463339934295.jpg/1]
fileName : 		 						
		 						
		 	2) 기존 파일 일부 삭제 > 기존 파일 일부 삭제
		 						삭제할 파일의 level 검사후 level이 0인 파일이 삭제되면
		 						다른기존 파일의 레벨을 0으로 재지정
		 						
Board(boardId=30, boardTitle=마술하나 보여줄까 ?, boardWriter=null, nickName=null, boardContent=우 후후후, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
[202501141046333954743.jpg/0, ]
fileName : 		 						
		 	3) 기존 파일 모두 유지 > borad만 수정		 
		 	
Board(boardId=30, boardTitle=마술하나 보여줄까 ?, boardWriter=null, nickName=null, boardContent=우 후후후, boardCount=0, createDate=null, modifyDate=null, status=null, boardType=0)
[, ]
fileName : 		 	
		  */
		
		b.setBoardType(2);
		
		// 새로 넣는 파일이 있다면 list에 옮겨닮는 작업 
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i = 0; i<files.size(); i++) {
			MultipartFile upload = files.get(i);
			
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload);
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					a.setRefBoardId(b.getBoardId());
					
					list.add(a);
				}
			}
		}
		
		// 삭제할 파일이 있따면 삭제할 파일의 이름과 레벨을 가각 delRename과 delLevel에 옮겨담기
		ArrayList<String> delRename = new ArrayList<String>();
		ArrayList<Integer> delLevel = new ArrayList<Integer>();
		for(String rename : deleteAttm) {
			if(!rename.equals("")) {
				String[] split = rename.split("/");
				delRename.add(split[0]);
				delLevel.add(Integer.parseInt(split[1]));
			}
		}
		
		int deleteAttmResult = 0;			// 파일 delete후 결과 값   
		int updateBoardResult = 0;			// 게시글 update 후 결과 값
		boolean existBeforeAttm = true;		// 이전 첨부파일이 존재하는지에 대한 여부
		
		if(!delRename.isEmpty()) {			// 저장했던 파일중 하나라도 삭제하겠다라는 경우
			deleteAttmResult = bService.deleteAttm(delRename);
			if(deleteAttmResult > 0) {
				for(String rename : delRename) {
					deleteFile(rename);
				}
			}
			
			if(deleteAttm.length !=0 && delRename.size() == deleteAttm.length) {			// 기존 파일을 모두 삭제했을때
				existBeforeAttm = false;
				if(list.isEmpty()) {
					b.setBoardType(1);
				}
			}else {
				for(int level : delLevel) {
					if(level == 0) {
						bService.updateAttmLevel(b.getBoardId());
						break;
					}
				}
			}
		}
		
		for(int i = 0; i <list.size(); i++) {
			Attachment a = list.get(i);
			if(existBeforeAttm) {
				a.setAttmLevel(1);
			}else {
				if(i==0) {
					a.setAttmLevel(0);
				}else {
					a.setAttmLevel(1);
				}
			}
		}
		
		
		updateBoardResult = bService.updateBoard(b);
		
		int updateAttmResult = 0;
		if(!list.isEmpty()) {
			updateAttmResult = bService.insertAttm(list);
		}
		
		if(updateBoardResult + updateAttmResult == list.size() + 1) {
			if(deleteAttm.length !=0 && delRename.size() == deleteAttm.length&&updateAttmResult==0) {
				return "redirect:/board/list";
			}else{
				return String.format("redirect:/attm/%d/%d", b.getBoardId(),page);
			}
		}else {
			throw new BoardException("첨부파일 게식슬 수정을 실패하였습니다.");
		}
		
		
	}
	
	
//	@PostMapping("delete")
//	public String deleteBoard(@RequestParam("boardId") int bId) {
//		int result1 = bService.deleteBoard(bId);
//		int result2 = bService.statusNAttm(bId);
//		
//		if(result1>0 && result2 >0) {
//			return "redirect:/attm/list";
//		}else {
//			throw new BoardException("첨부파일 게시글 삭제를 실패하였습니돠 ㅋㅋㅋ");
//		}
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
