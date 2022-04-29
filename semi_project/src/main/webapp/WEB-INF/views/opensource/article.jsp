﻿<%@ page contentType="text/html; charset=UTF-8" %>
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
.table-article tr > td {
	padding-left: 5px; padding-right: 5px;
}



.body-title h3 { 
	color:#243b73;
    border-bottom: 3px solid #243b73;
}
.table {
	color:#8f96a4;
}
.btn {
	border-radius: 4px; background-color:#8893aa ;
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

thead{
background: #f1f3f7;
color: #243b73;
font-weight: 700;
}

span > img {
	width: 50px; height: 50px;
}

span > img:hover {
	cursor: pointer;
}


.table-article tr > td {
	padding-left: 5px; padding-right: 5px;
}
</style>

<script type="text/javascript">
function deleteOpensource() {
	if(confirm('게시글을 삭제 하시겠습니까?')){
		let query = "num=${dto.num}&${query}";
		let url = "${pageContext.request.contextPath}/opensource/delete.do?"
		location.href = url + query;
	}
}

function likeClick() {
	
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
			<h3><i class="fas fa-clipboard-list"></i> 오픈소스 </h3> 
		</div>
        
		<table class="table table-border table-article">
			<thead>
				<tr>
					<td colspan="2" align="center">
						${dto.subject }
					</td>
				</tr>
			</thead>
			
			<tbody>
				<tr>
					<td width="50%">
						이름 : ${dto.userName }
					</td>
					<td align="right">
						${dto.reg_date } | 조회 ${dto.hitCount } | 좋아요 ${dto.likeCount }
					</td>
				</tr>
				
				<tr>
					<td colspan="2" valign="top" height="200">
						${dto.content }
					</td>
				</tr>
				
				<c:forEach var="vo" items="${listFile}">
				<tr>
					<td colspan="2">
						첨부 : 
						<a href="${pageContext.request.contextPath}/opensource/download.do?fileNum=${vo.fileNum}">${vo.originalFilename}</a>
					</td>
				</tr>
				</c:forEach>
				
				<tr>
					<td colspan="2">
						이전글 : 
						<c:if test="${not empty preReadOs}">
							<a href="${pageContext.request.contextPath}/opensource/article.do?${query}&num=${preReadOs.num}">${preReadOs.subject}</a>
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						다음글 : 
						<c:if test="${not empty nextReadOs}">
							<a href="${pageContext.request.contextPath}/opensource/article.do?${query}&num=${nextReadOs.num}">${nextReadOs.subject}</a>
						</c:if>
					</td>
				</tr>
			</tbody>
		</table>
		
		<table class="table">
			<tr>
				<td width="48%">
				<c:choose>
					<c:when test="${sessionScope.member.userId=='admin' && sessionScope.member.userId==dto.userId}">
						<button type="button" class="btn"  onclick="location.href='${pageContext.request.contextPath}/opensource/update.do?num=${dto.num}&${query}';">수정</button>
					</c:when>
					<c:otherwise>
						<button type="button" class="btn" disabled="disabled">수정</button>
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${sessionScope.member.userId=='admin' && sessionScope.member.userId==dto.userId}">
						<button type="button" class="btn" onclick="deleteOpensource();">삭제</button>
					</c:when>
					<c:otherwise>
						<button type="button" class="btn" disabled="disabled">삭제</button>
					</c:otherwise>
				</c:choose>
				</td>
				<td>
					<span><img src="${pageContext.request.contextPath}/resource/images/like_false.png" onclick="likeClick();"></span>
				</td>
				<td align="right">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/opensource/list.do?${query}';">리스트</button>
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