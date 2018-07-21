package com.sample.service;

import com.sample.dao.BoardDao;

public class BoardServiceImp implements BoardService {
	
	private BoardDao boardDao;

	public BoardServiceImp() {}

	public void setBoardDao(BoardDao boardDao) {
		this.boardDao = boardDao;
	}

}
