package com.sample.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import com.sample.dto.BoardDto;

public class BoardDaoImp implements BoardDao {
	
	private SqlSessionTemplate sqlSession;

	public BoardDaoImp() {}
	
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public void boardInsertProcess(BoardDto bdto) {
		// TODO Auto-generated method stub
		sqlSession.insert("board.boardInsertProcess", bdto);
	}

	@Override
	public List<BoardDto> boardList() {
		// TODO Auto-generated method stub
		return sqlSession.selectList("board.boardList");
	}

	@Override
	public BoardDto boardDetailView(int bNum) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("board.boardDetailForm", bNum);
	}

	@Override
	public void increaseViewCount(int bNum) {
		// TODO Auto-generated method stub
		sqlSession.update("board.increaseViewCount", bNum);	
	}

	@Override
	public void deleteDocument(int bNum) {
		// TODO Auto-generated method stub
		sqlSession.delete("board.deleteDocument", bNum);
	}
}
