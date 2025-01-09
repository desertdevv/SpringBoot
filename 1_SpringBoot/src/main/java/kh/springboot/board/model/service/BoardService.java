package kh.springboot.board.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor


public class BoardService {
	private final BoardMapper mapper;

	public int getListCount(int i) {
		return mapper.getListCount(i);
	}

	public ArrayList<Board> selectBoardList(PageInfo pi, int i) {
		int offset = (pi.getCurrentPage()-1) * pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mapper.selectBoardList(i, rowBounds);
	}

	public int insertBoard(Board b) {
		return mapper.insertBoard(b);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}

