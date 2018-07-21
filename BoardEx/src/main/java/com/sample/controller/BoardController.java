package com.sample.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@RequestMapping(value="boardDetailForm.do", method=RequestMethod.GET)
	public ModelAndView boardDetailForm(@RequestParam int bNum) {
		boardService.increaseViewCount(bNum);
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("board/boardDetailForm");
		mav.addObject("dto", boardService.boardDetailForm(bNum));
		return mav;
	}
	
	@RequestMapping(value="deleteDocument.do", method=RequestMethod.GET)
	public String deleteDocument(@RequestParam int bNum) {
		boardService.deleteDocument(bNum);
		
		return "redirect:/boardList.do";
	}
	
	@RequestMapping(value="updateDocument.do", method=RequestMethod.GET)
	public ModelAndView updateDocument(@RequestParam int bNum) {
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("board/boardUpdateForm");
		mav.addObject("dto", boardService.boardDetailForm(bNum));
		return mav;
	}
	
	@RequestMapping(value="updateEndDocument.do", method=RequestMethod.POST)
	public String updateEndDocument(BoardDto bdto) {
		boardService.updateEndDocument(bdto);
		
		return "redirect:/boardList.do";
	}
}
