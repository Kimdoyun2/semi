<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>dokky</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">


.table-form td {
	padding: 7px 0;
}
.table-form p {
	line-height: 200%;
}
.table-form tr:first-child {
	border-top: 2px solid #212529; 
}
.table-form tr > td:first-child {
	width: 110px; text-align: center; background: #f1f3f7 ; color:#243b73; 
}

.table-form tr > td:nth-child(2) {
	padding-left: 10px;
}

.table-form input[type=text], .table-form input[type=file], .table-form textarea {
	width: 96%;
}


a { color: #8f96a4; }

.body-title h3 { 
	color:#243b73;
    border-bottom: 3px solid #243b73;
}
.table {
	color:#8f96a4;
}
.btn {
	border-radius: 4px; background-color: #8893aa;
	color:#fff;
}

.form-select {
	border-radius: 4px; background-color: #8893aa;
	color:#fff;
}

footer { 
    color:#8f96a4;
}

a:active, a:hover { color:#243b73; text-decoration: underline; }

</style>
<script type="text/javascript">
function sendStudy() {
    const f = document.studyForm;
	let str;
	
    str = f.subject.value.trim();
    if(!str) {
        alert("제목을 입력하세요. ");
        f.subject.focus();
        return;
    }

    str = f.content.value.trim();
    if(!str) {
        alert("내용을 입력하세요. ");
        f.content.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/study/${mode}_ok.do";
    f.submit();
}
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 700px;">
		<div class="body-title">
			<h3><i class="fa-solid fa-users-between-lines"></i> 스터디 모집 </h3>
		</div>
        
		<form name="studyForm" method="post">
			<table class="table table-border table-form">
				<tr> 
					<td>제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
					<td> 
						<input type="text" name="subject" maxlength="100" class="form-control" value="${dto.subject}">
					</td>
				</tr>
				
			
				
				<tr> 
					<td>작성자</td>
					<td> 
						<p>${sessionScope.member.userName}</p>
					</td>
				</tr>
				
				<tr> 
					<td valign="top">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
					<td> 
						<textarea name="content" class="form-control">${dto.content}</textarea>
					</td>
				</tr>
			</table>
				
			<table class="table">
				<tr> 
					<td align="center">
						<button type="button" class="btn" onclick="sendStudy();">${mode=='update'?'수정완료':(mode=='reply'? '답변완료':'등록하기')}</button>
						<button type="reset" class="btn">다시입력</button>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/study/list.do';">${mode=='update'?'수정취소':(mode=='reply'? '답변취소':'등록취소')}</button>
						<c:if test="${mode=='update'}">
							<input type="hidden" name="num" value="${dto.num}">
							<input type="hidden" name="page" value="${page}">
						</c:if>
						<c:if test="${mode=='reply'}">
							<input type="hidden" name="groupNum" value="${dto.groupNum}">
							<input type="hidden" name="orderNo" value="${dto.orderNo}">
							<input type="hidden" name="depth" value="${dto.depth}">
							<input type="hidden" name="parent" value="${dto.num}">
							<input type="hidden" name="page" value="${page}">
						</c:if>
					</td>
				</tr>
			</table>
		</form>
        
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

</body>
</html>