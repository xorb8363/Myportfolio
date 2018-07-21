package com.sample.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sample.dto.BoardDto;
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
	public ModelAndView boardList() {
		List<BoardDto> list = boardService.boardListProcess();
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("board/boardListForm");
		mav.addObject("list", list);
		return mav;
	}

	@RequestMapping(value = "/boardInsertForm.do", method = RequestMethod.GET)
	public String boardInsertForm() {
		return "board/boardInsertForm";
	}
	
	@RequestMapping(value="boardInsertProcess.do", method=RequestMethod.POST)
	public String boardInsertProcess(BoardDto bdto) {
		boardService.boardInsertProcess(bdto);
		
		return "redirect:/boardList.do";
	}
}
