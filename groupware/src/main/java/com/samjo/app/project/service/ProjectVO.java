package com.samjo.app.project.service;

import java.util.Date;

import lombok.Data;

@Data
public class ProjectVO {
	
	// 업무공통
	private int taskNo;
	private String taskName;
	private String taskPurpose;
	private String taskCntn;
	private Date taskStartDt;
	private Date taskDueDt;
	private String taskType;
	private String standardNo;
	private String prjtMat;
	private String deptId;
	
	//상시업무기준
	private String reguId;
	private String creType;
	private String crePerd;
	private String active;
	private Date fileDt;
	private String respEmpId;
	
	//프로젝트
	private String prjtId;
	private String prjtName;
	private String prjtStat;
	private String smry;
	private String purpose;
	private Date prjtStartDt;
	private Date prjtDueDT;
	private String respMngrId;
 
	// 업무참여자
	private String taskEmpId;
	private String deptName;
	private String cmplt;
	
	// 업무문서
	private String docNo;
	
}