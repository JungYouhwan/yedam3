package com.samjo.app.ct.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.samjo.app.common.service.SearchVO;
import com.samjo.app.ct.mapper.CtMapper;
import com.samjo.app.ct.service.CtService;
import com.samjo.app.ct.service.CtVO;

@Service
public class CtServiceImpl implements CtService{

	@Value("${file.upload.path}")
	private String uploadPath;

	@Autowired
	CtMapper ctMapper;
	
	@Override
	public List<CtVO> ctList(SearchVO searchVO) {
		return ctMapper.selectCtAll(searchVO);
	}

	@Override
	public int count() {
		return ctMapper.ctCount();
	}

}