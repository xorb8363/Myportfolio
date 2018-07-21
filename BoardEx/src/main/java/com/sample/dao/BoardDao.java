package com.sample.dao;

import java.util.List;

import com.sample.dto.BoardDto;

public interface BoardDao {
	
	public void boardInsertProcess(BoardDto bdto);
	public List<BoardDto> boardList();
	public BoardDto boardDetailView(int bNum);
}
