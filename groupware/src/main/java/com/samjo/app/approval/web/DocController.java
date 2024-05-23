package com.samjo.app.approval.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.samjo.app.approval.service.DocService;
import com.samjo.app.approval.service.DocVO;
import com.samjo.app.approval.service.TempVO;
import com.samjo.app.common.service.PageDTO;
import com.samjo.app.common.service.SearchVO;
import com.samjo.app.common.util.SecuUtil;
import com.samjo.app.emp.service.EmpVO;
import com.samjo.app.project.service.ProjectVO;
import com.samjo.app.security.service.LoginUserVO;
import com.samjo.app.upload.service.FileUploadService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 전자결재 중 문서관리.
 * @author 권효주.
 * 
 *
 */
@Controller
@Data
@RequiredArgsConstructor
public class DocController {
	
	final DocService docService;
	final FileUploadService fileUploadService;
	
	/*
	 * @Autowired public DocController(DocService docService, FileUploadService
	 * fileUploadService) { this.docService = docService; this.fileUploadService =
	 * fileUploadService; }
	 */
	
	// 문서 전체조회.
	@GetMapping("docList")
	public String docList(SearchVO searchVO, Model model) {
		if(searchVO.getPage() == 0) {
			searchVO.setPage(1);
		}
		List<DocVO> list = docService.docList(searchVO);
		model.addAttribute("list", list);
		
		PageDTO pageDTO = new PageDTO(searchVO.getPage(), docService.count());
		model.addAttribute("pageDTO", pageDTO);
		
		return "approval/doc/list";
	}
	
	// 한 직원이 작성한 문서 전체 조회.
	@GetMapping("myDocList")
	public String myDocList(SearchVO searchVO, Model model,  
								Authentication authentication) {
		if(searchVO.getPage() == 0) {
			searchVO.setPage(1);
		}
		
		Object principal = authentication.getPrincipal();
		if (principal instanceof LoginUserVO) {
            LoginUserVO loginUserVO = (LoginUserVO) principal;
            
            String empId = loginUserVO.getEmpId();
            List<DocVO> list = docService.getMyDocList(empId, searchVO);
            PageDTO pageDTO = new PageDTO(searchVO.getPage(), docService.countEmpDocs(empId));
            
            model.addAttribute("list", list);
            model.addAttribute("pageDTO", pageDTO);
            model.addAttribute("path", "myDocList");
            
            return "approval/list/empDocs";
        } else {
        	System.out.println("Not principal instanceof LoginUserVO");
        	return "test/test";
        }
	}
	
	// 한 직원이 현재 결재해야할 문서리스트.
	@GetMapping("myAprList")
	public String myAprList(SearchVO searchVO, Model model, 
								Authentication authentication) {
		if(searchVO.getPage() == 0) {
			searchVO.setPage(1);
		}
		
		Object principal = authentication.getPrincipal();
		if (principal instanceof LoginUserVO) {
            LoginUserVO loginUserVO = (LoginUserVO) principal;
            
            String empId = loginUserVO.getEmpId();
            List<DocVO> list = docService.getMyAprList(empId, searchVO);
            PageDTO pageDTO = new PageDTO(searchVO.getPage(), docService.countEmpApr(empId));
            
            model.addAttribute("list", list);
            model.addAttribute("pageDTO", pageDTO);
            model.addAttribute("path", "myAprList");
            
            return "approval/list/empAprs";
        } else {
        	System.out.println("Not principal instanceof LoginUserVO");
        	return "test/test";
        }		
	}
	
	// 전체문서 중 결재진행 중 문서.
	@GetMapping("docIng")
	public String docIng(SearchVO searchVO, Model model, 
							Authentication authentication) {
		if(searchVO.getPage() == 0) {
			searchVO.setPage(1);
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof LoginUserVO) {
            LoginUserVO loginUserVO = (LoginUserVO) principal;
            
            String empId = loginUserVO.getEmpId();
            String custNo = loginUserVO.getCustNo();
            String deptId = loginUserVO.getDeptId();
            String permId = loginUserVO.getPermId();
            
            EmpVO empVO = new EmpVO(empId, custNo, deptId, permId);
            List<DocVO> list = docService.getIngDocList(empVO, searchVO);
            PageDTO pageDTO = new PageDTO(searchVO.getPage(), docService.countIng(empVO));
            
            model.addAttribute("list", list);
            model.addAttribute("pageDTO", pageDTO);
            model.addAttribute("path", "docIng");
            
            return "approval/list/ing";
        } else {
        	System.out.println("Not principal instanceof LoginUserVO");
        	return "test/test";
        }
	}
	
	// 전체문서 중 결재완료/반려 문서.
	@GetMapping("docCmplt")
	public String docCmplt(SearchVO searchVO, Model model) {
		
		searchVO = checkSearch(searchVO);
		EmpVO empVO = SecuUtil.getLoginEmp();
		
		
		if(empVO != null) {
			List<DocVO> list = docService.getCmpltDocList(empVO, searchVO);
			PageDTO pageDTO = new PageDTO(searchVO.getPage(), docService.countCmplt(empVO, searchVO));
			model.addAttribute("list", list);
			model.addAttribute("pageDTO", pageDTO);
			System.out.println("searchVO ==========> "+searchVO);
			model.addAttribute("search", searchVO);
			model.addAttribute("path", "docCmplt");
			return "approval/list/cmplt";
		} else {
			return "test/test";
		}
	}
	
	// 문서 상세정보.
	@GetMapping("docInfo")
	public String docInfo(DocVO docVO, Model model) {
		DocVO findVO = docService.docInfo(docVO);
		List<EmpVO> emps = docService.docRefs(docVO.getDocNo());
		List<ProjectVO> tasks = docService.docTasks(docVO.getDocNo());
		model.addAttribute("doc", findVO);
		model.addAttribute("tasks", tasks);
		model.addAttribute("emps", emps);
		
		return "approval/doc/info";
	}
	
	// 문서작성 양식.
	@GetMapping("docInsert")
	public String dobInsertForm(Model model) {
		//DocVO docVO = docService.getDocNo();
		List<TempVO> temps = docService.getCustTemps();
		model.addAttribute("doc", new DocVO());
		model.addAttribute("temps", temps);
		return "approval/doc/insert";
	}
	
	// 문서작성 저장.
	@PostMapping("docInsert")
	public String docInsertProcess(DocVO docVO, MultipartFile[] filelist) {
		int docNo = docService.docInfoInsert(docVO);
		if(!filelist[0].isEmpty()) {
			List<Map<String, Object>> flist = 
					fileUploadService.uploadFileInfo(filelist, docVO.getDraft(), docVO.getDocNo());
			int ret = docService.fileInsert(flist, docVO);
			System.out.println("file-insert-ret->"+ ret);
		}
		if(docNo != -1) {
			return "redirect:docInfo?docNo=" + docNo;
		}
		return "test/test";
	}
	
	// 문서수정 양식
	@GetMapping("docUpdate")
	public String docUpdateForm(@RequestParam Integer no, Model model) {
		DocVO docVO = new DocVO();
		docVO.setDocNo(no);
		DocVO findVO = docService.docInfo(docVO);
		model.addAttribute("doc", findVO);
		
		List<TempVO> temps = docService.getCustTemps();
		List<EmpVO> emps = docService.docRefs(docVO.getDocNo());
		List<ProjectVO> tasks = docService.docTasks(docVO.getDocNo());
		model.addAttribute("temps", temps);
		model.addAttribute("emps", emps);
		model.addAttribute("tasks", tasks);
		
		System.out.println(findVO);
		return "approval/doc/update";
	}
	
	// 문서수정 반영.
	@PostMapping("docUpdate")
	public String docUpdateProcess(DocVO docVO, MultipartFile[] filelist, String flag) {
		int update = docService.docInfoUpdate(docVO);
		int delete = -1;
		if(update > 0 && flag.equals("YES")) {
			delete = docService.fileDelete(docVO); //파일삭제와 파일테이블데이터삭제.
			if(delete >= 0) {
				// 파일삭제 성공(또는 삭제할파일 없음)  -> 새로운 파일 등록.
				if(docService.fileInsert(docVO, filelist) >= 0) {
					return "redirect:docInfo?docNo=" + docVO.getDocNo();
				};
			}
		} else if(update > 0 && flag == null) {
			return "redirect:docInfo?docNo=" + docVO.getDocNo();
		} else {
			return "test/test";
		}
		return "test/test";
	}
	
	
	
	public SearchVO checkSearch(SearchVO searchVO) {
		if(searchVO.getPage() == 0) {
			searchVO.setPage(1);
		}
		return searchVO;
	}
	
}
