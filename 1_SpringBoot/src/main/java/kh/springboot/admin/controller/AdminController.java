package kh.springboot.admin.controller;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private final BoardService bService;
	
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
	
	
	
}
