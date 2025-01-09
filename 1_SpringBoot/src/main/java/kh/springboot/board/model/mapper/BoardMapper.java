package kh.springboot.board.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;

@Mapper
public interface BoardMapper {

	int getListCount(int i);
	
	public ArrayList<Board> selectBoardList(int i, RowBounds rowBounds);

	int insertBoard(Board b);
	
	

}
