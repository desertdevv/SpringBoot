package kh.springboot.board.model.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;

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

	ArrayList<Board> selectTop();

	ArrayList<Reply> selectReplyList(int bId);

	int insertReply(Reply r);

	int deleteReply(int rId);

	int updateReply(Reply r);

	ArrayList<Board> selectRecentBoards();

	int updateBoardStatus(HashMap<String, Object> map);

	ArrayList<Attachment> selectAllAttm();





	
	

}
