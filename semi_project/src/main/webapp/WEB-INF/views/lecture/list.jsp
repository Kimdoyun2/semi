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
.table {
	color: #8f96a4;
}

.form-select {
	background-color: #8893aa; color:#fff;
	font-weight: bold; font-size: 14px;
}

.select-category {
	background-color: #243b73; color:#fff;
	width: 88px;
}

.table-list thead > tr:first-child{
	background: #f8f8f8;
}
.table-list th, .table-list td {
	text-align: center;
}
.table-list .left {
	text-align: left; padding-left: 5px; 
}

.table-list .category {
	width: 95px; background: #f1f3f7;
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

.table-list .lecture {
	display: inline-block; padding: 1px 3px; color: #fff; background: #243b73;
	width: 56px;
}

</style>
<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
	f.submit();
}

function changeOrder() {
	let order = document.getElementById('order-select').value;
	let category = document.getElementById('category-select').value;
	
	if (order == "latest") {
		if (category == "") {
			location.href = "${pageContext.request.contextPath}/lecture/list.do";
		} else {
			location.href = "${pageContext.request.contextPath}/lecture/list.do?category="+category;
		}
	} else if (order == "view") {
		if (category == "") {
			location.href="${pageContext.request.contextPath}/lecture/list.do?order="+order;
		} else {
			location.href="${pageContext.request.contextPath}/lecture/list.do?order="+order+"&category="+category;
		}
	}
}

function changeCategory() {
	let order = document.getElementById('order-select').value;
	let category = document.getElementById('category-select').value;
	
	if (order == "latest") {
		if (category == "") {
			location.href = "${pageContext.request.contextPath}/lecture/list.do";
		} else {
			location.href = "${pageContext.request.contextPath}/lecture/list.do?category="+category;
		}
	} else if (order == "view") {
		if (category == "") {
			location.href="${pageContext.request.contextPath}/lecture/list.do?order="+order;
		} else {
			location.href="${pageContext.request.contextPath}/lecture/list.do?order="+order+"&category="+category;
		}
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
			<h3><i class="fas fa-book"></i> 강의 </h3>
		</div>
        
		<table class="table">
			<tr>
				<td width="50%">${dataCount}개(${page}/${total_page} 페이지)</td>
				<td align="right">
					<select id="order-select" name="order" class="form-select" onchange="changeOrder();">
						<option value="latest" ${order=="latest"?"selected='selected'":""}>최신순</option>
						<option value="view" ${order=="view"?"selected='selected'":""}>조회순</option>
					</select>
				</td>
			</tr>
		</table>
		
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="category">
						<select id="category-select" name="category" class="form-select select-category" onchange="changeCategory();">
							<option value="">말머리</option>
							<option value="1" ${category==1 ? "selected='selected'" : ""}>Java</option>
							<option value="2" ${category==2 ? "selected='selected'" : ""}>Android</option>
							<option value="3" ${category==3 ? "selected='selected'" : ""}>C++</option>
							<option value="4" ${category==4 ? "selected='selected'" : ""}>기타</option>
					  	</select>
					</th>
					<th class="num">번호</th>
					<th class="subject">제목</th>
					<th class="name">작성자</th>
					<th class="date">작성일</th>
					<th class="hit">조회수</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach var="dto" items="${list}">
				<tr>
					<td>
						<span class="lecture">
							${dto.lecture==1?"Java":dto.lecture==2?"Android":dto.lecture==3?"C++":dto.lecture==4?"기타":""}
						</span>
					</td>
					<td>${dto.listNum}</td>
					<td class="left">
						<a href="${articleUrl}&num=${dto.num}">${dto.subject}</a>
					</td> 
					<td>${dto.userName}</td>
					<td>${dto.reg_date}</td>
					<td>${dto.hitCount}</td>
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
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/lecture/list.do';">새로고침</button>
				</td>
				<td align="center">
					<form name="searchForm" action="${pageContext.request.contextPath}/lecture/list.do" method="post">
						<select name="condition" class="form-select">
							<option value="all" ${condition=="all" ?"selected='selected'":"" }>제목+내용</option>
							<option value="userName" ${condition=="userName" ?"selected='selected'":"" }>작성자</option>
							<option value="reg_date" ${condition=="reg_date" ?"selected='selected'":"" }>등록일</option>
							<option value="subject" ${condition=="subject" ?"selected='selected'":"" }>제목</option>
							<option value="content" ${condition=="content" ?"selected='selected'":"" }>내용</option>
						</select>
						<input type="text" name="keyword" value="${keyword}" class="form-control">
						<button type="button" class="btn" onclick="searchList();">검색</button>
					</form>
				</td>
				<td align="right" width="100">
					<c:if test="${sessionScope.member.userId =='admin' }">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/lecture/write.do';">글올리기</button>
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