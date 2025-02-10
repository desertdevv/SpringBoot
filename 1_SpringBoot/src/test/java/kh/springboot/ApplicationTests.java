package kh.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kh.springboot.board.controller.BoardController;
import kh.springboot.board.model.vo.Board;

@SpringBootTest
class ApplicationTests {
	
	@Autowired
	private BoardController controller;
	
	@BeforeAll
	public static void startTest() {
		System.out.println("테스트를 시작합니다.");
	}
	
	@Test
	void contextLoads() {
		Board b = new Board(1,"test...","user01","건강퇴초","...test",0,
				new Date(new java.util.Date().getTime()),
				new Date(new java.util.Date().getTime()),"Y",1);
		//assertEquals() : 두 값을 비교해서 일치 여부 판단
		assertEquals("redirect:/board/1/1", controller.updateBoard(b, 1));
		
	}
	
	@AfterAll
	public static void endTest() {
		System.out.println("테스트를 종료합니다.");
	}
	
	
	
	
	
	//assertArrayEquals() : 두 배열을 비교하여 일치 여부 판단
	// assertNotNull() / assertNull() : 객체의 null 여부확인
	// assertTrue() / assertFalse(): 특정 조건인 true인지 false인인지 판단

}
