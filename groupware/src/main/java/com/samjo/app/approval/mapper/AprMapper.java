package com.samjo.app.approval.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.samjo.app.approval.service.AprVO;
import com.samjo.app.approval.service.DocVO;
import com.samjo.app.emp.service.EmpVO;

public interface AprMapper {
	
	// 결재자등록(DocService에서 사용).
	public int insertApr(@Param("doc") DocVO docVO);
	// 결재자삭제
	public int deleteApr(@Param("dno")Integer docNo);
	
	// 결재자 리스트.
	public List<AprVO> selectDocApr(@Param("dno")Integer docNo);
	
	// 상신하기(프로시저는 void사용).
	public void updateAprGo(AprVO aprVO);
	// 결재하기.
	public void updateAprOk(AprVO aprVO);
	// 반려하기.
	public void updateAprNg(AprVO aprVO);
	
	// 참조자 등록(DocService에서 사용).
	public int insertRef(@Param("doc") DocVO docVO);
	// 참조자 삭제
	public int deleteRef(@Param("dno")Integer docNo);
	// 참조자 가져오기.
	public List<EmpVO> selectDocRefs(@Param("dno")Integer docNo);

}
