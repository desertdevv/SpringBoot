package kh.springboot.board.controller;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
		
		ArrayList<Attachment> aList = bService.selectAttmBoardList();
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
		System.out.println(b);
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
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size()+1) {
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
	
	
}
