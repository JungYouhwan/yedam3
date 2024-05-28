package com.samjo.app.project.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.samjo.app.common.util.SecuUtil;
import com.samjo.app.emp.service.DeptService;
import com.samjo.app.emp.service.DeptVO;
import com.samjo.app.emp.service.EmpVO;
import com.samjo.app.project.service.ProjectVO;
import com.samjo.app.project.service.ReguService;

@Controller
public class ReguController {
	
	ReguService reguService;
	DeptService deptService;
	
	@Autowired
	public ReguController(ReguService reguService, DeptService deptService) {
		this.reguService = reguService;
		this.deptService = deptService;
	}
	
	// 상시업무등록 - 양식.
	@GetMapping("reguInsert")
	public String reguInsertForm(Model model) {
		
		EmpVO empVO = SecuUtil.getLoginEmp();
		
		if(empVO != null) {
			
			List<EmpVO> resp = deptService.respMngrList(empVO.getCustNo()); // 책임자.
			DeptVO dept = deptService.myDeptEmps(empVO.getDeptId()); // 우리부서 모든사원.
			List<ProjectVO> regus = reguService.reguStadList(empVO.getCustNo()); // 기존상시업무목록.
			
			model.addAttribute("resp", resp);					
			model.addAttribute("dept", dept);					
			model.addAttribute("regus", regus);					
			model.addAttribute("taskRegu", new ProjectVO());
			
			return "project/regu/insert";
			
		} else {
			
			return "test/test";
		}
	}
	
	// 상시업무등록 - 등록.
	@PostMapping("reguInsert")
	public String Process(ProjectVO projectVO, String flag) {
		int ret = 0;
		
		// (flag == YES) => 기존 상시업무에 등록.
		if(flag.equals("YES")) {
			ret = reguService.reguCommonInsert(projectVO);
			System.out.println("기존 상시업무 등록결과 --------> " + ret);			
		} else if(flag.equals("NO")) {
			ret = reguService.reguStadInsert(projectVO);
			System.out.println("처음 상시업무 등록결과 --------> " + ret);			
		}
		
		return "test/test";
	}
	

}
