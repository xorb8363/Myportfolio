package com.sample.dao;

import org.mybatis.spring.SqlSessionTemplate;

public class BoardDaoImp implements BoardDao {
	
	private SqlSessionTemplate sqlSession;

	public BoardDaoImp() {}
	
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}
}
