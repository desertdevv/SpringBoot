package kh.springboot.common;

import kh.springboot.board.model.vo.PageInfo;

public class Pagination {
	public static PageInfo getPageInfo(int currentPage, int listCount, int boardLimit) {
		int pageLimit = 10;
		
		int maxPage = (int)Math.ceil((double)listCount / boardLimit);
		//ceil < 올림
		
		int startPage = (currentPage-1) / pageLimit * pageLimit +1;
		
		int endPage = startPage + pageLimit -1;
		if(maxPage <endPage) {
			endPage = maxPage;
		}
		
		return new PageInfo(currentPage, listCount, pageLimit, maxPage, startPage, endPage, boardLimit);
	}
}
