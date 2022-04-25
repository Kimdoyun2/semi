<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>dokky</title>
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
					<img class="user" src="http://assets.stickpng.com/images/585e4bf3cb11b227491c339a.png">
				</td>
				<td class="first">이름</td>
				<td class="second">김도키</td>
			</tr>
			<tr>
				<td class="first">등급</td>
				<td class="second">회원</td>
			</tr>
			<tr>
				<td class="first">가입일</td>
				<td class="second">2022.04.22</td>
			</tr>
			<tr>
				<td class="first">작성글</td>
				<td class="second">1</td>
			</tr>		
		</table>
		
		<h4>작성글</h4>
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="subject">제목</th>
					<th class="date">작성일</th>
					<th class="hit">조회수</th>
				</tr>
			</thead>
			
			<tbody>
					<tr>
						<td>국비학원 세미 프로젝트</td>
						<td>2022.04.22</td>
						<td>1</td>
					</tr>
			</tbody>
			
		</table>
		
		<div class="page-box">
			1 2 3
		</div>
		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/list.do';">새로고침</button>
				</td>
				<td align="center">
					<form name="searchForm" action="${pageContext.request.contextPath}/notice/list.do" method="post">
						<select name="condition" class="form-select">
							<option value="all" ${condition=="all"?"selected='selected'":""}>제목+내용</option>
							<option value="userName" ${condition=="userName"?"selected='selected'":""}>작성자</option>
							<option value="reg_date" ${condition=="reg_date"?"selected='selected'":""}>등록일</option>
							<option value="subject" ${condition=="subject"?"selected='selected'":""}>제목</option>
							<option value="content" ${condition=="content"?"selected='selected'":""}>내용</option>
						</select>
						<input type="text" name="keyword" value="${keyword}" class="form-control">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
					<c:if test="${sessionScope.member.userId == 'admin'}">						
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/notice/write.do';">글올리기</button>
					</c:if>
				</td>
			</tr>
		</table>	

	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>


</body>
</html>