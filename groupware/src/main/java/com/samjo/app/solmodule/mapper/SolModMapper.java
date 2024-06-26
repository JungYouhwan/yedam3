package com.samjo.app.solmodule.mapper;

import java.util.List;

import com.samjo.app.approval.service.TempVO;
import com.samjo.app.common.service.SearchVO;
import com.samjo.app.cust.service.CustVO;
import com.samjo.app.solmodule.service.ModuleVO;

public interface SolModMapper {
	
	//모듈 전체조회
	public List<ModuleVO> selectModAll();
	
	//고객사 전체조회 모달용
	public List<CustVO> selectCustAll();
	
	//템플릿 등록처리
	public int inserTemp(TempVO tempVO);
	
	//템플릿 전체조회
	public List<TempVO> selectTempAll(SearchVO searchVO);
	
	//템플릿 상세조회
	public TempVO selectTemp(String tempNo);
	
	//템플릿 페이징
	public int tempCount();
	
	//템플릿 수정
	public int UpdateTemp(TempVO tempVO);
	
	//템플릿 삭제
	public int DeleteTemp(TempVO tempVO);
	
}
