package kh.springboot.board.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.vo.Member;
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

//	public Board selectBoard(Member m, int bId) {
//		Board b = mapper.selectBoard(bId);
//		
//		if(m==null || b.getBoardWriter().equals(m.getId())) {
//			return b;
//		}else {
//			int result = mapper.updateBoardCount(bId);
//			if(result>0) {
//				return mapper.selectBoard(bId);
//			}else {
//				return null;
//			}
//		}
//		//성훈이 풀이
//	}

	public Board selectBoard(int bId, String id) {
		Board b = mapper.selectBoard(bId);
		
		if(b != null && id != null && b.getBoardWriter().equals(id)) {
			int result = mapper.updateCount(bId);
			if(result>0) {
				b.setBoardCount(b.getBoardCount()+1);
			}
		
		}
		return mapper.selectBoard(bId);
	}

	public int updateBoard(Board b) {
		return mapper.updateBoard(b);
	}

	public int deleteBoard(int bId) {
		return mapper.deleteBoard(bId);
	}

	public ArrayList<Attachment> selectAttmBoardList(Integer bId) {
		return mapper.selectAttmBoardList(bId);
	}

	public int insertAttm(ArrayList<Attachment> list) {
//		int result = 0;
//		for(Attachment a : list) {
//		result += mapper.insertAttm(list);
//		}
//		return result;
//	}
		
		return mapper.insertAttm(list);

	}

	public int deleteAttm(ArrayList<String> delRename) {
		return mapper.deleteAttm(delRename);
	}

	public void updateAttmLevel(int boardId) {
		mapper.updateAttmLevel(boardId);
	}



//	public int statusNAttm(int bId) {
//		return mapper.statusNAttm(bId);
//	}
	
	
	public ArrayList<Board> selectTop() {
		return mapper.selectTop();
	}

	public ArrayList<Reply> selectReplyList(int bId) {
		return mapper.selectReplyList(bId);
	}

	public int insertReply(Reply r) {
		return mapper.insertReply(r);
	}	
	
	
	
	
	
	
	
}

