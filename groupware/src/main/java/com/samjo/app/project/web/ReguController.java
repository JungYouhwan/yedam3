package com.samjo.app.project.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.samjo.app.approval.service.DocVO;
import com.samjo.app.common.service.PageDTO;
import com.samjo.app.common.service.SearchVO;
import com.samjo.app.common.util.SecuUtil;
import com.samjo.app.emp.service.DeptService;
import com.samjo.app.emp.service.DeptVO;
import com.samjo.app.emp.service.EmpVO;
import com.samjo.app.project.service.ProjectVO;
import com.samjo.app.project.service.ReguService;
import com.samjo.app.project.service.TaskEmpsVO;

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
	@GetMapping("cust/regu/insert")
	public String reguInsertForm(Model model) {
		
		EmpVO empVO = SecuUtil.getLoginEmp();
		
		if(empVO != null) {
			
			List<EmpVO> resp = deptService.myDeptMngrs(empVO); // 책임자.
			List<DeptVO> depts = deptService.myDeptEmps(empVO); // 우리부서 모든사원.				
			List<ProjectVO> regus = reguService.reguStadList(empVO); // 기존상시업무목록.
			model.addAttribute("resp", resp);					
			model.addAttribute("depts", depts);					
			model.addAttribute("regus", regus);					
			model.addAttribute("taskRegu", new ProjectVO());
			
			return "project/regu/insert";
			
		} else {
			
			return "test/test";
		}
	}
	
	// 상시업무등록 - 등록.
	@PostMapping("cust/regu/insert")
	public String reguInsertProcess(ProjectVO regu, String flag) {
		String result = null;
		
		// (flag == YES) => 기존 상시업무에 등록.
		if(flag.equals("YES")) {
			
			result = reguService.reguCommonInsert(regu);
			if(result != null) {
				return "redirect:/cust/regu/stadInfo?reguId=" + regu.getReguId();
			}
			
		} else if(flag.equals("NO")) {
			
			result = reguService.reguStadInsert(regu);
			if(result != null) {
				return "redirect:/cust/regu/stadInfo?reguId=" + regu.getReguId();
			}
			
		} 
		
		return "test/test";
	}
	
	// 단건조회
	@GetMapping("cust/regu/info")
	public String reguInfo(Model model, @RequestParam Integer taskNo, SearchVO searchVO) {
		
		EmpVO empVO = SecuUtil.getLoginEmp();
		
		if(empVO != null) {
			
			ProjectVO findVO = reguService.reguInfo(empVO.getCustNo(), taskNo);
			model.addAttribute("regu", findVO);
			
			List<DocVO> docs = reguService.taskDocList(taskNo);
			model.addAttribute("docs", docs);
			
			return "project/regu/info";
			
		} else {
			
			return "test/test";
		}
	}
	
	// 담당업무 완료
	@ResponseBody
	@PutMapping("cust/regu/info/ok/{taskNo}")
	public Map<String, Object> reguCmpltModify(@PathVariable Integer taskNo, 
												@RequestBody List<TaskEmpsVO> emps) {
		
		return reguService.reguCmpltModify(emps);
	}
	
	// 전체조회(하위)
	@GetMapping("cust/regu/list")
	public String reguTaskList(SearchVO searchVO, Model model) {
		
		checkSearch(searchVO);
		EmpVO empVO = SecuUtil.getLoginEmp();
		if(searchVO.getSortCondition().equals("tc.standard_no")) {
			searchVO.setSortCondition("tc.task_no DESC");
		}
		
		if(empVO != null) {
			
			List<ProjectVO> list = reguService.reguTaskList(empVO, searchVO);
			int count = reguService.countReguTasks(empVO, searchVO);
			PageDTO pageDTO = new PageDTO(searchVO.getPage(), count);
			
			model.addAttribute("list", list);
			model.addAttribute("pageDTO", pageDTO);
			model.addAttribute("search", searchVO);
			model.addAttribute("path", "cust/regu/list"); 
			
			return "project/regu/list";
			
		} else {
			
			return "test/test";
		}
		
	}
	
	// 전체조회(하위) - 검색
	@PostMapping("cust/regu/list/sch")
	public String reguTaskSearchList(SearchVO searchVO, Model model) {
		
		checkSearch(searchVO);
		EmpVO empVO = SecuUtil.getLoginEmp();
		if(searchVO.getSortCondition().equals("tc.standard_no")) {
			searchVO.setSortCondition("tc.task_no DESC");
		}
		
		if(empVO != null) {
			
			List<ProjectVO> list = reguService.reguTaskList(empVO, searchVO);
			int count = reguService.countReguTasks(empVO, searchVO);
			PageDTO pageDTO = new PageDTO(searchVO.getPage(), count);
			
			model.addAttribute("list", list);
			model.addAttribute("pageDTO", pageDTO);
			model.addAttribute("search", searchVO);
			model.addAttribute("path", "list/sch"); 
			
			return "project/regu/list :: #ReguListArea";
			
		} else {
			
			return "test/test";
		}
		
	}
	
	// 전체조회(상위)
	@GetMapping("cust/regu/stadList")
	public String reguStadList(SearchVO searchVO, Model model) {
		
		checkSearch(searchVO);
		EmpVO empVO = SecuUtil.getLoginEmp();
		
		if(empVO != null) {
			
			List<ProjectVO> list = reguService.reguList(empVO, searchVO);
			
			int count = reguService.countRegus(empVO, searchVO);
			PageDTO pageDTO = new PageDTO(searchVO.getPage(), count);			
			List<DeptVO> depts = deptService.myCustDepts(empVO);				
			
			model.addAttribute("list", list);
			model.addAttribute("pageDTO", pageDTO);
			model.addAttribute("search", searchVO);
			model.addAttribute("depts", depts);
			model.addAttribute("path", "stadList"); 
			
			return "project/regu/stadList";				
			
		}
	
		return "test/test";
	}
	
	// 전체조회(상위) - 검색
	@PostMapping("cust/regu/stadList/sch")
	public String reguStadSearchList(SearchVO searchVO, Model model) {
		
		checkSearch(searchVO);
		EmpVO empVO = SecuUtil.getLoginEmp();
		
		if(empVO != null) {
			
			List<ProjectVO> list = reguService.reguList(empVO, searchVO);
			
			int count = reguService.countRegus(empVO, searchVO);
			PageDTO pageDTO = new PageDTO(searchVO.getPage(), count);
			
			model.addAttribute("list", list);
			model.addAttribute("pageDTO", pageDTO);
			model.addAttribute("search", searchVO);
			model.addAttribute("path", "stadList/sch"); 
			
			return "project/regu/stadList :: #stadListArea";				
			
		}
	
		return "test/test";
	}
	
	// 단건조회(상위)
	@GetMapping("cust/regu/stadInfo")
	public String reguStadInfo(Model model, @RequestParam String reguId) {
		
		EmpVO empVO = SecuUtil.getLoginEmp();
		SearchVO search = new SearchVO();
		checkSearch(search);
		
		if(empVO != null) {
			
			ProjectVO regu = reguService.reguStadInfo(empVO, reguId);
			model.addAttribute("regu", regu);
			
			search.setKeywordCondition("regu_id");
			search.setKeyword(reguId);
			search.setSortCondition("tc.task_no DESC");
			List<ProjectVO> list = reguService.reguTaskList(empVO, search);
			model.addAttribute("tasks", list); 
			
			int count = reguService.countRegus(empVO, search);
			PageDTO pageDTO = new PageDTO(search.getPage(), count);
			model.addAttribute("pageDTO", pageDTO); 
			
			model.addAttribute("path", "stadInfo"); 
			
			return "project/regu/stadInfo";				
			
		}
	
		return "test/test";
	}
	
	// 수정 - 양식.
	@GetMapping("cust/regu/update")
	public String reguUpdateForm(Model model, @RequestParam String reguId) {
		EmpVO empVO = SecuUtil.getLoginEmp();
		
		if(empVO != null) {
			
			ProjectVO regu = reguService.reguStadInfo(empVO, reguId);
			model.addAttribute("regu", regu);
			
			return "project/regu/stadUpdate";
			
		} else {
			
			return "test/test";
		}
	}
	
	// 수정 - 처리.
	@PostMapping("cust/regu/update")
	@ResponseBody
	public Map<String, Object> reguUpdateProcess(@RequestBody ProjectVO regu) {
		return reguService.reguUpdate(regu);
	}
	
	// SearchVO check
	public SearchVO checkSearch(SearchVO searchVO) {
		if(searchVO.getPage() == 0) {
			searchVO.setPage(1);
		}
		if(searchVO.getSortCondition() == null) {
			searchVO.setSortCondition("tc.standard_no");
		}
		return searchVO;
	}

}
