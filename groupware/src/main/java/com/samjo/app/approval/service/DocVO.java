package com.samjo.app.approval.service;

import java.util.Date;
import java.util.List;

import com.samjo.app.project.service.ProjectVO;

import lombok.Data;

@Data
public class DocVO {
	private Integer docNo;
	private String draft;
	private String custNo;
	private String deptId;
	private String title;
	private String cntn;
	private Date draftDt;
	private String draftStat;
	private String tempId;
	private Integer finalLine;
	private Integer nowLine;
	private String docStat;
    private Date cmpltDt;
    
    // doc 테이블 항목 외 추가.
    private String draftName; //기안자이름.
    private String deptName; //기안자의부서명.
    private String draftStatName; //작성상태 부코드명.
    private String docStatName; //문서상태 부코드명.
    private String tempName; // 템플릿명.
    
    // 조회시 필요한 필드 추가.
    private String curApr;
    private String task;
    
    // 결재자 리스트.
    private List<AprVO> aprs;
    
    // 첨부파일 리스트.
    private List<DocFileVO> files;
    
    // 관련업무 리스트.
    private List<ProjectVO> taskList;
    
    // 참조자 리스트, 업무 리스트.
    private List<String> refs; // 참조자의 empId만 필요.
    private List<Integer> tasks; // 업무번호.
    
    // 휴가원
    private TempVO pto;
    
}
