package com.sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sample.service.BoardService;

@Controller
public class BoardController {

	private BoardService boardService;

	public BoardController() {
	}

	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}

	@RequestMapping(value = "/boardList.do", method = RequestMethod.GET)
	public String boardList() {
		return "board/boardListForm";
	}

	@RequestMapping(value = "/boardInsertForm.do", method = RequestMethod.GET)
	public String boardInsertForm() {
		return "board/boardInsertForm";
	}
}
