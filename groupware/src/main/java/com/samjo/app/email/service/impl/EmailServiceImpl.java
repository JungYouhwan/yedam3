package com.samjo.app.email.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samjo.app.common.service.SearchVO;
import com.samjo.app.email.mapper.EmailMapper;
import com.samjo.app.email.service.EmailFileVO;
import com.samjo.app.email.service.EmailService;
import com.samjo.app.email.service.EmailVO;
import com.samjo.app.emp.service.EmpVO;

@Service
public class EmailServiceImpl implements EmailService {
	
	
	EmailMapper emailMapper;
	
	@Autowired
	public EmailServiceImpl(EmailMapper emailMapper) {
		this.emailMapper = emailMapper;
	}
	
	
	//받은메일함 전체조회
	@Override
	public List<EmailVO> inboxList(SearchVO searchVO) {
		return emailMapper.selectInboxAll(searchVO);
	}

	//받은메일함 상세조회
	@Override
	public EmailVO inboxInfo(String senEmailNo) {
		EmailVO emailVO = new EmailVO();
		emailVO = emailMapper.selectInbox(senEmailNo);
		emailVO.setFiles(emailMapper.selectEmailFile(senEmailNo));
		return emailVO;
	}
	
	//메일 발송, 파일 업로드를 한 트랜잭션에.
	//단. 효주님은 인서트를 여기 다 몰았고, 나는 프로시저 안에 몰았다.
	//그러므로, 파일 업로드 외에는 inserEmail로 다 처리됨
	@Transactional
	@Override
	public int emailInsert(EmailVO emailVO, List<Map<String, Object>> fileInfoList) {
		//메일 내용 인서트 해주고.
		int result = emailMapper.insertEmail(emailVO);
		//메일 첨부파일 인서트
		if(fileInfoList.size() > 0) {
			List<EmailFileVO> emailFileList = getEmailFileList(emailVO, fileInfoList);
			emailMapper.insertEmailFile(emailFileList);
		}
		return result;
	}
	
	@Override
	public List<EmpVO> getEmpList(EmpVO empVO) {
		return emailMapper.getEmpList(empVO);
	}
	
	//메일 첨부파일.. 버린 메소드
	@Override
	public int EmailFileInsert(EmailFileVO emailFileVO) {
		//emailMapper.insertEmailFile(emailFileVO);
		return -1;
	}
	
	//보낸메일함 전체조회
	@Override
	public List<EmailVO> emailList(SearchVO searchVO) {
		return emailMapper.selectEmailAll(searchVO);
	}

	//보낸메일함 상세조회
	@Override
	public EmailVO emailInfo(String senEmailNo) {
		EmailVO emailVO = new EmailVO();
		emailVO = emailMapper.selectEmail(senEmailNo);
		emailVO.setFiles(emailMapper.selectEmailFile(senEmailNo));
		return emailVO;
	}

	// 보낸 메일 휴지통 이동(상태 칼럼값 변경)
	@Override
	public int deleteEmail(List<String> senEmailNoList) {
		return emailMapper.deleteEmail(senEmailNoList);
	}
	
	// 받은 메일 휴지통 이동
	@Override
	public int deleteInbox(List<String> recEmailNoList) {
		return emailMapper.deleteInbox(recEmailNoList);
	}
	
    @Override
    public void restoreMail(List<String> senEmailNoList) {
        for (String senEmailNo : senEmailNoList) {
            Map<String, Object> params = new HashMap<>();
            params.put("senEmailNo", senEmailNo);
            emailMapper.restoreMail(params);            
        }
        
    }
	
	//메일 완전삭제
	@Override
	public void removeMail(List<String> senEmailNoList) {
        for (String senEmailNo : senEmailNoList) {
            Map<String, Object> params = new HashMap<>();
            params.put("senEmailNo", senEmailNo);
            emailMapper.removeMail(params);            
        }
        
    }
	
	// 답신할 대상의 메일정보(수신메일)를 가져오기(체인메일넘버 유념)
	@Override
	public EmailVO getInboxNo(EmailVO emailVO) {
		return emailMapper.getInboxNo(emailVO);
	}

	//주고받은메일리스트 조회
	@Override
	public List<EmailVO> chainMailList() {
		return emailMapper.chainMailList();
	}

	//발신메일에 파일 첨부 파일 업로드와 다운로드는 COMMON에서 처리할 듯 하다. 
	//서비스 로직 구현을 여기에서 하지 않을 가능성이 크다는 소리다. 일단 건들지말고 두자.
	@Override
	public EmailVO setFile(EmailVO emailVO) {
		return null;
	}

	//수신메일에서 파일 다운로드
	@Override
	public EmailVO getFile(EmailVO emailVO) {
		return null;
	}
	
	//무의미 -> 테스트후 삭제
	@Override
	public int count(String empId) {
		return emailMapper.count(empId);
	}

	@Override
	public int countSend() {
		return emailMapper.countSend();
	}

	@Override
	public List<EmailVO> wastedList(SearchVO searchVO) {
		return emailMapper.wastedList(searchVO);
	}

	@Override
	public int countWasted(SearchVO searchVO) {
		return emailMapper.countWasted(searchVO);
	}

	// List<Map<String, Object>> => EmailFileVO.
	public List<EmailFileVO> getEmailFileList(EmailVO emailVO, List<Map<String, Object>> fileInfoList) {
		
		List<EmailFileVO> emailFileList = new ArrayList<>();
		
		fileInfoList.forEach(file -> { // object 형변환해서 fileVO 생성.
			EmailFileVO emailFileVO =
					new EmailFileVO(emailVO.getSenEmailNo(), (String) file.get("saveName"), 
									(String) file.get("uplName"), (String) file.get("fileExt"), 
									(Long) file.get("fileSize"), emailVO.getSender());
			emailFileList.add(emailFileVO);
		});
		
		return emailFileList;
	}
	
	//받은메일 페이징
	@Override
	public int countMyInbox(SearchVO searchVO) {
		return emailMapper.countMyInbox(searchVO);
	}

	@Override
	public int countMyEmail(SearchVO searchVO) {
		return emailMapper.countMyEmail(searchVO);
	}







	

}
