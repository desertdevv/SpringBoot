package kh.springboot.board.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;

@Mapper
public interface BoardMapper {

	int getListCount(int i);
	
	public ArrayList<Board> selectBoardList(int i, RowBounds rowBounds);

	int insertBoard(Board b);

	Board selectBoard(int bId);

//	int updateBoardCount(int bId);ㄴ

	int updateCount(int bId);

	int updateBoard(Board b);

	int deleteBoard(int bId);

	ArrayList<Attachment> selectAttmBoardList(Integer bId);

	int insertAttm(ArrayList<Attachment> list);

	int deleteAttm(ArrayList<String> delRename);

	void updateAttmLevel(int boardId);

//	int statusNAttm(int bId);
	
	

}
