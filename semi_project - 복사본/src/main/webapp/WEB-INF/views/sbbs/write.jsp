<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
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
.table-form tr > td:nth-child(2) {
	padding-left: 10px;
}

.table-form input[type=text], .table-form input[type=file], .table-form textarea {
	width: 96%;
}
</style>

<script type="text/javascript">
function sendOk() {
    const f = document.boardForm;
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

    f.action = "${pageContext.request.contextPath}/sbbs/${mode}_ok.do";
    f.submit();
}

<c:if test="${mode=='update'}">
	function deleteFile(num) {
		if( !confirm("파일을 삭제하시겠습니까 ?") ) {
			return;
		}
		let url = "${pageContext.request.contextPath}/sbbs/deleteFile.do?num=" + num + "&page=${page}";
		location.href = url;
	}
</c:if>
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 700px;">
		<div class="body-title">
			<h3><i class="fas fa-graduation-cap"></i> 프로그래밍 강좌 </h3>
		</div>
        
		<form name="boardForm" method="post" enctype="multipart/form-data">
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
				
				<tr>
					<td>첨&nbsp;&nbsp;&nbsp;&nbsp;부</td>
					<td> 
						<input type="file" name="selectFile" class="form-control">
					</td>
				</tr>
				<c:if test="${mode=='update'}">
					<tr>
						<td>첨부된파일</td>
						<td> 
							<p>
								<c:if test="${not empty dto.saveFilename}">
									<a href="javascript:deleteFile('${dto.num}');"><i class="far fa-trash-alt"></i></a>
									${dto.originalFilename}
								</c:if>
								&nbsp;
							</p>
						</td>
					</tr>
				</c:if>
			</table>
				
			<table class="table">
				<tr> 
					<td align="center">
						<button type="button" class="btn" onclick="sendOk();">${mode=='update'?'수정완료':'등록하기'}</button>
						<button type="reset" class="btn">다시입력</button>
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/sbbs/list.do';">${mode=='update'?'수정취소':'등록취소'}</button>
						<c:if test="${mode=='update'}">
							<input type="hidden" name="num" value="${dto.num}">
							<input type="hidden" name="page" value="${page}">
							<input type="hidden" name="fileSize" value="${dto.fileSize}">
							<input type="hidden" name="saveFilename" value="${dto.saveFilename}">
							<input type="hidden" name="originalFilename" value="${dto.originalFilename}">
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

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>