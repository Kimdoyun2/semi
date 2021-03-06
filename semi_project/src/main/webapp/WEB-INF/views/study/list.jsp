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

.table-list thead > tr:first-child{
	background: #f8f8f8;
}
.table-list th, .table-list td {
	text-align: center;
}
.table-list .left {
	text-align: left; padding-left: 15px; 
}

.table-list .cruit {
	width: 70px; background: #f1f3f7; color: #243b73;
}
.table-list .num {
	width: 50px; background: #f1f3f7; color: #243b73; 
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


a:active, a:hover { color:#243b73; text-decoration: underline; }

.table-list .recruit{
	display: inline-block; padding: 1px 3px; background: #243b73 ; color: #fff; margin-left:9px; margin-top:9px;
}
.table-list .notrecruit{
	display: inline-block; padding: 1px 3px; background: #f1f3f7 ; color: #243b73; margin-left:9px; margin-top:9px;
}
</style>

<script type="text/javascript">
function searchList() {
	const f = document.searchForm;
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
			<h3><i class="fa-solid fa-users-between-lines"></i> ????????? ?????? </h3>
		</div>
        
		<table class="table">
			<tr>
				<td width="50%">
					${dataCount}??? | (${page}/${total_page} ?????????)
				</td>
				<td align="right">
					<select name="complete" class="form-select">
						<option value="1" ${complete==1 ? "selected='selected' ":""}>?????????</option>
					</select>
				<td>
			</tr>
		</table>
		
		<table class="table table-border table-list">
			<thead>
				<tr>
					<th class="cruit">????????????</th>
					<th class="num">??????</th>
					<th class="subject">??????</th>
					<th class="name">?????????</th>
					<th class="date">?????????</th>
					<th class="hit">?????????</th>
				</tr>
			</thead>
			
	
			<tbody>
					<c:forEach var="dto" items="${list}">
						<tr>
							<c:choose>
							<c:when test="${dto.recruit!=0}"><td class="recruit"><span>?????????</span></td></c:when>
							<c:otherwise><td class="notrecruit"><span>?????????</span></td></c:otherwise>
							</c:choose>
							
							<td>${dto.listNum}</td>
							<td class="left">
								<c:forEach var="n" begin="1" end="${dto.depth }">&nbsp;&nbsp;</c:forEach>
								<c:if test="${dto.depth!=0}">L Re:&nbsp;</c:if>
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
			${dataCount == 0 ? "????????? ???????????? ????????????." : paging}
		</div>
		
		<table class="table">
			<tr>
				<td width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/study/list.do';" title="????????????">????????????</button>
				</td>
				<td align="center">
					<form name="searchForm" action="${pageContext.request.contextPath}/study/list.do" method="post">
						<select name="condition" class="form-select">
							<option value="all"      ${condition=="all"?"selected='selected'":"" }>??????+??????</option>
							<option value="userName" ${condition=="userName"?"selected='selected'":"" }>?????????</option>
							<option value="reg_date"  ${condition=="reg_date"?"selected='selected'":"" }>?????????</option>
							<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>??????</option>
							<option value="content"  ${condition=="content"?"selected='selected'":"" }>??????</option>
						</select>
						<input type="text" name="keyword" value="${keyword}" class="form-control">
						<button type="button" class="btn" onclick="searchList();">??????</button>
					</form>
				</td>
				<td align="right" width="100">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/study/write.do';">????????????</button>
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