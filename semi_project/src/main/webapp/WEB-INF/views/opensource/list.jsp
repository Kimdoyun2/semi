<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>dokky</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">




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


a:active, a:hover { color:#243b73; text-decoration: underline; }



.table-list thead > tr:first-child{
	background: #f8f8f8;
}
.table-list th, .table-list td {
	text-align: center;
}
.table-list .left {
	text-align: left; padding-left: 5px; 
}

.table-list .num {
	width: 60px; background: #f1f3f7; color: #243b73;
}
.table-list .subject {
	background: #f1f3f7; color: #243b73;
}
.table-list .name {
	width: 100px; background: #f1f3f7; color: #243b73;
}
.table-list .date {
	width: 100px; background: #f1f3f7; color: #243b73;
}
.table-list .hit {
	width: 70px; background: #f1f3f7; color: #243b73;
}
.table-list .like {
	width: 70px; background: #f1f3f7; color: #243b73;
}


a:active, a:hover { color:#243b73; text-decoration: underline; }


.table-list .notice{
	display: inline-block; padding: 1px 3px; background: #243b73 ; color: #fff;
}

</style>
<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}

function orderChange() {
	let selectedValue = document.getElementById('orderselected').value;
	let keyword = document.getElementById('keyword').value;
	if(keyword === undefined) {
		location.href="${pageContext.request.contextPath}/opensource/list.do?order="+selectedValue;
	} else {
		location.href="${pageContext.request.contextPath}/opensource/list.do?order="+selectedValue
		+"&condition=${condition}&keyword="+keyword;
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
			<h3><i class="fas fa-clipboard"></i> 오픈소스 </h3>
		</div>
        
		<table class="table">
			<tr>
				<td width="50%">${dataCount}개(${page}/${total_page} 페이지)</td>
				<td align="right">
					<select id="orderselected" name="order" class="order-select" onchange="orderChange()">
							<option value="latest" ${order=="latest" ?"selected='selected'":"" }>최신순</option>
							<option value="hitCount" ${order=="hitCount" ?"selected='selected'":"" }>조회수순</option>
							<option value="likeCount" ${order=="likeCount" ?"selected='selected'":"" }>좋아요순</option>
					</select>
				</td>
			</tr>
		</table>
		
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="num">번호</th>
					<th class="subject">제목</th>
					<th class="name">작성자</th>
					<th class="date">작성일</th>
					<th class="hit">조회수</th>
					<th class="like">좋아요</th>
				</tr>
			</thead>
			
			<tbody>
			
				<c:forEach var="dto" items="${list}">
				<tr>
					<td>${dto.listNum}</td>
					<td class="left">
						<a href="${articleUrl}&num=${dto.num}">${dto.subject}</a>
					</td> 
					<td>${dto.userName}</td>
					<td>${dto.reg_date}</td>
					<td>${dto.hitCount}</td>
					<td>${dto.likeCount}</td>
				</tr>
				</c:forEach>
			
			</tbody>
			
		</table>
		
		<div class="page-box">
			${dataCount == 0 ? "등록된 게시물이 없습니다." : paging }
		</div>
 		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/opensource/list.do';">새로고침</button>
				</td>
				<td align="center">
					<form name="searchForm" action="${pageContext.request.contextPath}/opensource/list.do" method="post">
						<select name="condition" class="form-select">
							<option value="all" ${condition=="all" ?"selected='selected'":"" }>제목+내용</option>
							<option value="userName" ${condition=="userName" ?"selected='selected'":"" }>작성자</option>
							<option value="reg_date" ${condition=="reg_date" ?"selected='selected'":"" }>등록일</option>
							<option value="subject" ${condition=="subject" ?"selected='selected'":"" }>제목</option>
							<option value="content" ${condition=="content" ?"selected='selected'":"" }>내용</option>
						</select>
						<input type="text" name="keyword" id="keyword" value="${keyword }" class="form-control">
						<input type="hidden" name="order" value="${order}">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/opensource/write.do';">글올리기</button>
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