package com.samjo.app.work.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
@Data
public class WorkManagerSearchVO {

	private int page;
	private int filte;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date oneDate;
	private int totalCnt;
}