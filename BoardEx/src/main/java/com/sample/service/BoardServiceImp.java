package com.sample.service;

import java.util.List;

import com.sample.dao.BoardDao;
import com.sample.dto.BoardDto;

public class BoardServiceImp implements BoardService {
	
	private BoardDao boardDao;

	public BoardServiceImp() {}

	public void setBoardDao(BoardDao boardDao) {
		this.boardDao = boardDao;
	}

	@Override
	public void boardInsertProcess(BoardDto bdto) {
		// TODO Auto-generated method stub
		boardDao.boardInsertProcess(bdto);
	}

	@Override
	public List<BoardDto> boardListProcess() {
		// TODO Auto-generated method stub
		return boardDao.boardList();
	}

	@Override
	public BoardDto boardDetailForm(int bNum) {
		// TODO Auto-generated method stub
		return boardDao.boardDetailView(bNum);
	}

}
