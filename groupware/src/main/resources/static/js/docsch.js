/* doc.js */

console.log('static/js/docsch.js');

$(function(){

	//기간 검색 범위처리
	$('.fromDt').on('change', function(e){
		$('.toDt').attr('min', this.value);
	});
	$('.toDt').on('change', function(e){
		$('.fromDt').attr('max', this.value);
	});
	
	//검색버튼 이벤트 처리
	$(".searchbtn").on("click", function(e) {
		let page = 1;
		showTable(page);
	});
	
	//페이징 링크 처리
	$(document).on("click", ".pagebtn", function(e){
		e.preventDefault();
		let page = e.target.id;
		showTable(page);
	});
	
	//검색 AJAX 함수
	function showTable(page){
		let custNo;
		let ctName;
		let ctNo;
		let ctStat;
		let ctDt = $('.ctDt').val();
		let ctStart = $('.fromDt').val();
		let ctEnd = $('.toDt').val();
		let ct;
		//키워드 검색 처리
		if($('.searchKeySelect').val()=='custname'){
			ctName = $('.searchKey').val();
		}else if($('.searchKeySelect').val()=='custno'){
			custNo = $('.searchKey').val();
		}else if($('.searchKeySelect').val()=='ctno'){
			ctNo = $('.searchKey').val();
		}
		//기간 검색 처리
		if($('.searchKeySelect').val()=='custname'){
			ctName = $('.searchKey').val();
			
		}else if($('.searchKeySelect').val()=='custno'){
			custNo = $('.searchKey').val();
		}
		//계약상태 검색처리
		if($('.ctStat').val()!='statAll'){
			ctStat = $('.ctStat').val();
		}
		//정렬 처리
		let ctSort = $('.searchSelect').val();
		
		$.ajax({
			url : '/sol/viewCtList',
			type : 'POST',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			data : {
				page : page,
				ctName : ctName,
				ctSort : ctSort,
				custNo : custNo,
				ctNo : ctNo,
				ctStart : ctStart,
				ctEnd : ctEnd,
				ctStat : ctStat,
				ctDt : ctDt
			},
			dataType : 'Text',
			success : function(result) {
				console.log(result);
				$('#ctTable').replaceWith(result);
			}
		});
	}

}) //end

/* 업무 name부여 */
const schTaskName = () => {
	$('input.aleadyInputs').remove();

	let inputsNo = $('#getTasks').find('input[name="no"]');
	inputsNo.each(function(i,e) {
		e.setAttribute('name','schTaskNo[' + i + ']');
	});
	
	let inputsNa = $('#getTasks').find('input[name="na"]');
	inputsNa.each(function(i,e) {
		e.setAttribute('name','schTaskName[' + i + ']');
	});
	
}

