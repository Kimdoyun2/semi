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
.guest-form {
	clear: both;
    border: #d5d5d5 solid 1px;
    padding: 15px;
    min-height: 50px;	
}
.guest-form > .form-header {
	padding-bottom: 7px;
}
.guest-form > .form-header > .bold {
	font-weight: bold;
}
.guest-form > .form-body {
	padding: 10px 0 3px;
}
.guest-form > .form-footer {
	padding-top: 3px; text-align: right;
}
.guest-form textarea {
	width: 100%; height: 75px;
}
.guest-form button {
	padding: 8px 25px;
}

.list-header {
	padding-top: 25px; padding-bottom: 7px;
}
.list-header .guest-count {
	color: #3EA9CD; font-weight: 700;
}

.guest-list {
	table-layout: fixed; word-break: break-all;
}
.guest-list tr:nth-child(2n+1) {
	border: 1px solid #ccc; background: #f8f8f8;
}
.guest-list td {
	padding-left: 7px; padding-right: 7px;
}
</style>
<script type="text/javascript">
function sendGuest() {
	const uid = "${sessionScope.member.userId}";
	if(! uid) {
		location.href = "${pageContext.request.contextPath}/member/login.do";
		return;
	}
	
	const f = document.guestForm;
	let str;
	
	str = f.content.value.trim();
	if(!str) {
		alert("내용을 입력 하세요 !!!");
		f.content.focus();
		return;
	}
	
	f.action = "${pageContext.request.contextPath}/guest/guest_ok.do";
	f.submit();
}

function deleteGuest(num) {
	let url = "${pageContext.request.contextPath}/guest/delete.do?num=" + num + "&page=${page}";
	
	if(confirm("게시글을 삭제 하시겠습니까 ?")) {
		location.href = url;
	}
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
			<h3><i class="far fa-edit"></i> 방명록 </h3>
		</div>
        
		<form name="guestForm" method="post">
			<div class="guest-form">
				<div class="form-header">
					<span class="bold">방명록쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가해 주세요.</span>
				</div>
				<div class="form-body">
					<textarea name="content" id="content" class="form-control"></textarea>
				</div>
				<div class="form-footer">
					<button type="button" class="btn" onclick="sendGuest();"> 등록하기 </button>
				</div>
			</div>
		</form>
		
		<c:if test="${dataCount != 0}">
			<div class="list-header">
				<span class="guest-count">방명록 ${dataCount}개</span>
				<span>[목록, ${page}/${total_page} 페이지]</span>
			</div>
			
			<table class="table guest-list">
				<c:forEach var="dto" items="${list}">
					<tr>
						<td width='50%'>
							<span>작성자 : ${dto.userName}</span>
						</td>
						<td width='50%' align='right'>
							${dto.reg_date} 
							<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
								| <a href="javascript:deleteGuest('${dto.num}');">삭제</a>
							</c:if>
						</td>
					</tr>
					<tr>
						<td colspan='2' valign='top'>${dto.content}</td>
					</tr>
				</c:forEach>
			</table>
			
			<div class="page-box">
				${paging}
			</div>
		</c:if>
		
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>