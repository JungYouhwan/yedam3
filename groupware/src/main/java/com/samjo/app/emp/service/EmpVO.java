package com.samjo.app.emp.service;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EmpVO {
	private String empId;
	private String ctNo;
	private String custNo;
	private String deptId;
	private String jobNo;
	private String empNo;
	private String pw;
	private String empName;
	private Date hireDt;
	private Date FireDt;
	private String empStat;
	private String signImg;
	private String empAddr1;
	private String empAddr2;
	private String empAddr3;
	private String empAddr4;
	private String empTel;
	private String emailAddr;
	
	// 직급명, 부서명
	private String jobTitle;
	private String deptName;
	
	// 권한.
	private String permId;
	private String permName;
	
	// 조회 위한 생성자..
	public EmpVO(String empId, String custNo, String deptId, String permId) {
		this.empId = empId;
		this.custNo = custNo;
		this.deptId = deptId;
		this.permId = permId;
	}
	
	
	 
}
