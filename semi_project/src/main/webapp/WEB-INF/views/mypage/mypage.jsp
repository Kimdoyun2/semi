<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="icon" href="data:;base64,iVBORw0KGgo=">
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.table-mypage {
	margin-bottom: 20px;
	background: #f1f3f7;
	height: 150px;
}

.table-mypage tr:first-child {
	border-top: 1px solid #eee;
}

.table-mypage td {
	padding-top: 0px;
	padding-bottom: 0px;
}

.first, .second {
	padding-left: 5px;
}

.first {
	width: 60px;
	color: #243b73; font-weight: bold;
}

.second {
}

.table-list thead>tr:first-child {
	background: #f8f9fa;
}
.table-list tr>th, .table-list tr>td {
	text-align: center;
}
.table-list .left {
	text-align: left; padding-left: 5px; 
}
.table-list .num {
	width: 60px; color: #787878;
}
.table-list .subject {
	color: #787878;
}
.table-list .date {
	width: 100px; color: #787878;
}
.table-list .hit {
	width: 70px; color: #787878;
}

.user {
	width: 150px;
}

</style>

</head>
<body>
<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>

<main>
	<div class="body-container" style="width: 700px;">
		<div class="body-title">
			<h3><i class="fas fa-user"></i> 마이페이지 </h3>
		</div>
		
		<table class="table table-mypage">
			<tr>
				<td rowspan="4" width="170px">
					<img class="user" src="${pageContext.request.contextPath}/resource/images/user.png">
				</td>
				<td class="first">이름</td>
				<td class="second">${dto.userName}</td>
			</tr>
			<tr>
				<td class="first">등급</td>
				<td class="second">${dto.grade}</td>
			</tr>
			<tr>
				<td class="first">가입일</td>
				<td class="second">${dto.register_date}</td>
			</tr>
			<tr>
				<td class="first">작성글</td>
				<td class="second">${boardCount}개</td>
			</tr>		
		</table>
		
		<h4>작성글[ ${page} 페이지 | ${total_page} 페이지 ]</h4>
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="subject">제목</th>
					<th class="date">작성일</th>
					<th class="hit">조회수</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach var="vo" items="${list}">
					<tr>
						<td>
						<a href="${pageContext.request.contextPath}/${vo.boardName}/article.do?num=${vo.num}&page=1">${vo.subject}</a>
						</td>
						<td>${vo.reg_date}</td>
						<td>${vo.hitCount}</td>
					</tr>
				</c:forEach>
			
			</tbody>
			
		</table>
		
		<div class="page-box">
			${paging}
		</div>

	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>

</body>
</html>