/* approval.js */

console.log('static/js/approval.js');

$(function(){
			
			// input태그정리
			$('.input-tag-css input').css('border','none').css('width','50px').css('background','transparent');
			
			// custTemp, cntn 숨기기
			$('div#temparea').hide();
			$('textarea#cntn').hide();
			$('#finalLine').val(1);
			
			// 템플릿 선택.
			$('#tempId').on('change', function(e){
				
				$('.cntn-slide').slideUp();
				// custTemp 초기화, 숨기기.
				$('div#custTemp').html('');
				$('div#temparea').slideUp();
				// cntn 초기화, 숨기기.
				$('textarea#cntn').html('');
				$('textarea#cntn').slideUp();
				// 휴가원,지출결의 숨기기
				$('#tempPTO').hide();
				$('#tempEXT').hide();
				
				// 옵션값 확인하기
				let opt = $('#tempId option:selected');
				console.log(opt.val());
				
				// 옵션값 - 1.선택안함
				if(opt.val() == 'no-data') {
					$('.cntn-slide').slideDown();
					return;
				}
				
				if(opt.val() == 'TP002') {
					$('#tempPTO').show();
					return;
				}
				
				if(opt.val() == 'TP001') {
					$('#tempEXT').show();
					return;
				}
				
				// 옵션값 - 2.기안문선택 / 3.템플릿선택
				let tempPath = $('#tempId option:selected').data('temp');
				if(tempPath == 'no-path') {
					$('textarea#cntn').slideDown();
				} else {
					$('div#temparea').slideDown();
					$.ajax({
						url : 'files/' + tempPath,
						dataType: "html",
						success: function(html) {
							$('div#custTemp').html(html);
	            	    },
	            	    error: function(err){
	            	    	console.log('ERR : ', err);
	            	    }
					})
				}
			})// 템플릿 선택.
			
			// 모달열기 - 결재자.
			$('.doc-modal-btn-apr').on('click', function(e) {
				let dept = $('input[name="deptId"]').val();
				$('#selectapr .modal-body').load("/cust/deptEmps?deptId="+ dept);
				$('#selectapr').modal();
			})
			
			// 모달에서 선택된 결재자를 가져오기.
			let ctn;
			$('.selbtn').on('click', function(e) {
				let trs = $('#aprlist > tbody > tr');
				let spanTag = $('#selaprs');
				spanTag.children().remove();
				ctn = 0;
				
				trs.each(function(idx,tr){
					let get = $(tr);
					
					let creatInputTag = $(`<p>
											<input name="aprs[${(idx+1)}].aprSq" type="text"
											 value="${get.find('td').eq(1).text()}" readonly />번
											<input name="aprs[${(idx+1)}].aprEmp" type="hidden"
											 value="${get.data('emp')}" readonly />
											<input name="aprs[${(idx+1)}].aprName" type="text"
											 value="${get.find('td').eq(2).text()}" readonly />
										   </p>`);
					
					spanTag.append(creatInputTag);
					ctn++;
				});
				$('#finalLine').val(++ctn);
				$('.input-tag-css input').css('border','none').css('width','50px').css('background','transparent');
			})
			
			// 모달열기 - 참조자.
			$('.doc-modal-btn-refs').on('click', function(e) {
				let cust = $('input[name="custNo"]').val();
				$('#docModalRefs .modal-body').load("/cust/custEmps?custNo="+ cust);
				$('#docModalRefs').modal();				
			})
			
			// 모달에서 선택한 참조자 가져오기.
			$('.getRefs-btn').on('click', getDocRefs);
			
			// 모달열기 - 업무.
			$('.doc-modal-btn-tasks').on('click', function(e) {
				let cust = $('input[name="custNo"]').val();
				$('#docModalTasks .modal-body').load("cust/custTasks?custNo="+ cust);
				$('#docModalTasks').modal();				
			})
			
			// 모달에서 업무 가져오기.
			$('.getTasks-btn').on('click', getDocTsks);
			
			// 작성완료 버튼.
			$('#docInsertForm').submit(async function(e){
				// submit 중지.
				e.preventDefault();
				const submitDoc = this;
				
				// 필수입력 확인.
				if(!docCheck()) return;
				
				// 문서등록.
				//  1.작성한 템플릿을 저장하고 내용(cntn)에 경로입력.=>getPath()
				//  2.첨부파일을 저장.=>saveFile();
				if(confirm('저장할까요?')) {
					try {
						
						let cntnTemp = $('div#custTemp').html();
						let okPath;
						if(cntnTemp != '') {
							okPath = await getPath(cntnTemp);
							$('textarea#cntn').val(okPath);
							console.log('템플릿완료');
						}
						
						let fileInput = document.querySelector('input#docFiles');
						let fileList = fileInput.files;
					 	let okFile;
					 	if(fileList.length != 0) {
					 		okFile = await saveFile(fileList);
					 		console.log('첨부파일완료');
					 		
					 		okFile.forEach((ele,idx) => {
					 			fileInfo = ele.split(':::');
					 			let fUpl = $('<input name="files[' + (idx) + '].uplName" type="hidden" />');
					 			fUpl.val(fileInfo[0]);
					 			let fExt = $('<input name="files[' + (idx) + '].fileExt" type="hidden" />');
					 			fExt.val(fileInfo[1]);
					 			let fSize = $('<input name="files[' + (idx) + '].fileSize" type="hidden" />');
					 			fSize.val(fileInfo[2]);
					 			let fSave = $('<input name="files[' + (idx) + '].saveName" type="hidden" />');
					 			fSave.val(fileInfo[3]);
					 			$('#docFiles').append(fUpl, fExt, fSize, fSave);
					 		})
					 		
					 	}
					 	
					 	alert('문서등록 완료.');
			            this.submit();
			        
					} catch (error) {
			            console.error(error);
			            alert('작업 중 오류가 발생했습니다.');
			        }
				}
			})// 작성완료버튼.
			
		})// end.
		
		// 필수입력 항목선택.
		function docCheck(e) {
			if($('#title').val() == '') {
				alert('제목은 필수입력 항목입니다.');
				$('#title').focus();
				return false;
			}
			if($('#tempId').val() == 'no-data') {
				alert('템플릿을 선택해주세요.');
				$('#tempId').focus();
				return false;
			}
			return true;
		}
		
		// 참조자 모달 함수(참조자 가져오기 함수)
		function getDocRefs() {
			let trs = $('#refslist > tbody > tr');
			let divTag = $('#getRefs');
			divTag.children().remove();
			
			trs.each(function(idx,tr){
				let get = $(tr);
				
				let creatTag = $(`<p>
									<input name="refs[${(idx)}]" type="hidden"
										   value="${get.data('emp')}" readonly />
									<span>${get.find('td').eq(1).text()}
									      ${get.find('td').eq(2).text()}
									      ${get.find('td').eq(3).text()}</span>
								  </p>`);
				divTag.append(creatTag);			
			})
		}
		
		// 업무연결 모달 함수.
		function getDocTsks() {
			let trs = $('#list3 > tbody input[name=chk-list3]:checked').closest('tr');
			let divTag = $('#getTasks');
			divTag.children().remove();
			
			trs.each(function(idx,tr) {
				let get = $(tr);
				let data = get.data('task');
				let creatTag = $(`<p>
									<input name="tasks[${(idx)}]" type="hidden"
										   value="${get.data('task')}" readonly />
									<span>${get.find('td').eq(0).text()} 
									      ${get.find('td').eq(1).text()}</span>
								  </p>`);
				divTag.append(creatTag);
			});
		}
		
		// 저장경로 가져와서 내용(cntn)에 담기.
		const getPath = (cntnTemp) => { // 파라미터로 파일리스트 받기(위 okPath)
			// 결과를 프로미즈 객체에 담기.
			return new Promise((resolve, reject) => {
				// 파일이름 정의.
	    		let fileName = '테스트' + $('input[name="docNo"]').val();
				// 아작스로 파일업로드하고 경로리턴받기.
			    $.ajax({
			        url: 'uploadsHtml',    
			        type: 'POST',
			        data: JSON.stringify({ tags: cntnTemp, fileName: fileName }),
			        dataType: 'text',
			        contentType: 'application/json;charset=UTF-8'
			    })
			    .done(response => {
			        resolve(response);
			    })
			    .fail(err => {
			        alert('템플릿 저장 중 오류.');
			        reject(err);
			    });
			})
		}
		
		const saveFile = (fileList) => { // 파라미터로 파일리스트 받기(위 okFile)
			// 결과를 프로미즈 객체에 담기.
			return new Promise((resolve, reject) => {
				// FormData() 인스턴스.
			    let formData = new FormData(); 

				// formData에 담기
			    for(let file of fileList) {
			        formData.append('uploadFiles', file);
			    }					
				
				// 아작스로 파일업로드하고 경로리턴받기.
			    $.ajax({
			        url: 'uploadFiles',    
			        type: 'POST',
			        processData: false, 
			        contentType: false,
			        data: formData,
			    })
			    .done(response => {
			        resolve(response);
			    })
			    .fail(err => {
			        alert('첨부파일 저장 중 오류');
			        reject(err);
			    });
			})// promise.
		}
