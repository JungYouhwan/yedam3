package com.samjo.app.project.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samjo.app.approval.service.DocVO;
import com.samjo.app.common.service.SearchVO;
import com.samjo.app.emp.service.EmpVO;
import com.samjo.app.project.mapper.ReguMapper;
import com.samjo.app.project.service.ProjectVO;
import com.samjo.app.project.service.ReguService;
import com.samjo.app.project.service.TaskEmpsVO;

@Service
public class ReguServiceImpl implements ReguService {

	ReguMapper reguMapper;
	
	@Autowired
	public ReguServiceImpl(ReguMapper reguMapper) {
		this.reguMapper = reguMapper;
	}
	
	// 처음 상시업무 등록.
	@Override
	@Transactional
	public String reguStadInsert(ProjectVO regu) {
		int result = 0;
		
		result = reguMapper.insertRegu(regu);
		if(result != 1) {
			return null;
		}
		
		result = 0;	
		
		result = reguMapper.insertReguTaskEmp(regu);
		if(result == 0) {
			return null;	
		}
		
		return regu.getReguId();
	}
	
	// 기존 상시업무 등록.
	@Override
	public String reguCommonInsert(ProjectVO regu) {
		int result = 0;
		
		result = reguMapper.insertReguTaskEmp(regu);
		if(result == 0) {
			return null;
		}
		
		return regu.getReguId();
	}
	
	// 기존 상시업무목록 가져오기.
	@Override
	public List<ProjectVO> reguStadList(EmpVO empVO) {
		return reguMapper.selectReguAll(empVO);
	}

	// 단건조회(하위)
	@Override
	public ProjectVO reguInfo(String custNo, Integer taskNo) {
		ProjectVO findVO = reguMapper.selectRegu(custNo, taskNo);
		
		if(findVO == null) {
			return null;
		}
		
		// 진행률 계산
		double total = findVO.getTaskEmps().size();
		int cmplt = 0;
		
		for(TaskEmpsVO te : findVO.getTaskEmps()) {
			cmplt += te.getResult();
		}
		
		double per =  ((double) cmplt / total) * 100;
		int result = (int) Math.floor(per);
		
		// vo에 담기.
		findVO.setProgress(result);
		findVO.setParticipantsCnt((int) total);
		
		return findVO;
	}
	
	// 담당업무 완료처리.
	@Override
	public Map<String, Object> reguCmpltModify(List<TaskEmpsVO> emps) {
		Map<String, Object> map = new HashMap<>();
		
		for(TaskEmpsVO emp : emps) {
			
			reguMapper.reguTaskEmpOk(emp);
			int result = emp.getResult();
			map.put(emp.getTaskEmpId(), result);
			
		}
		
		return map;
	}

	// 전체조회(하위)
	@Override
	public List<ProjectVO> reguTaskList(EmpVO empVO, SearchVO search) {
		return reguMapper.selectReguTaskAll(empVO, search);
	}
	@Override
	public int countReguTasks(EmpVO empVO, SearchVO search) {
		return reguMapper.countReguTasks(empVO, search);
	}
	
	// 전체조회(상위)
	@Override
	public List<ProjectVO> reguList(EmpVO empVO, SearchVO search) {
		return reguMapper.selectReguStadAll(empVO, search);
	}
	@Override
	public int countRegus(EmpVO empVO, SearchVO search) {
		return reguMapper.countRegus(empVO, search);
	}

	// 단건조회(상위)
	@Override
	public ProjectVO reguStadInfo(EmpVO empVO, String reguId) {
		return reguMapper.selectReguStad(empVO, reguId);
	}
	
	// 단건조회(하위)의 연결문서
	@Override
	public List<DocVO> taskDocList(Integer taskNo) {
		return reguMapper.selectTaskDocs(taskNo);
	}
	
	// 수정
	@Override
	@Transactional
	public Map<String, Object> reguUpdate(ProjectVO ruge) {
		Map<String, Object> map = new HashMap<>();
		map.put("success", "ERROR");
		int result = 0;
		
		result = reguMapper.updateReguStad(ruge);
		if(result == 1) {
			map.put("stad", result);
			result = 0;
		} else {
			map.put("success", "NG");
			map.put("msg", "수정실패. 업무ID 확인필요.");
			return map;
		}
		
		result = reguMapper.updateTasks(ruge);
		if(result > 0) {
			map.put("tasks", result);
			map.put("success", "OK");
			map.put("msg", "수정완료.");
		} else {
			map.put("success", "NG");
			map.put("msg", "수정실패. 업무번호 확인필요.");
		}
		
		return map;
	}

}
