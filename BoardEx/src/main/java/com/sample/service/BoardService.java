package com.sample.service;

import java.util.List;

import com.sample.dto.BoardDto;

public interface BoardService {
	
	public void boardInsertProcess(BoardDto bdto);
	public List<BoardDto> boardListProcess();

}
