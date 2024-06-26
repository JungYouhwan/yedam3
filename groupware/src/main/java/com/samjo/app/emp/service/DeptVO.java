package com.samjo.app.emp.service;

import java.util.List;

import lombok.Data;

@Data
public class DeptVO {
	private String deptId;
	private String deptName;
	private String custNo;
	private String deptLoc;
	private String deptInfo;
	private String deptMngr;
	
	// 조회
	private String mngrName;
	private Integer cnt;
	
	private List<EmpVO> emps;
}
