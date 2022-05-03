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
.table-article tr > td {
	padding-left: 5px; padding-right: 5px;
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
.table-article tr > td {
	padding-left: 5px; padding-right: 5px;
}

</style>
<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
	function deleteqANDa() {
	    if(confirm("게시글을 삭제 하시겠습니까? 확인을 누르면 삭제 됩니다. ")) {
	        let query = "num=${dto.num}&${query}";
	        let url = "${pageContext.request.contextPath}/qANDa/delete.do?" + query;
	    	location.href = url;
	    }
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
			<h3><i class="fas fa-chalkboard-teacher"></i> 질문과 답변 </h3>
		</div>
        
		<table class="table table-border table-article">
			<thead>
				<tr>
					<td colspan="2" align="center">
						<c:if test="${dto.depth!=0 }">[Re]</c:if>
						${dto.subject}
					</td>
				</tr>
			</thead>
			
			<tbody>
				<tr>
					<td width="50%">
						이름 : ${dto.userName}
					</td>
					<td align="right">
						${dto.reg_date} | 조회 ${dto.hitCount}
					</td>
				</tr>
				
				<tr>
					<td colspan="2" valign="top" height="200">
						${dto.content}
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						이전글 :
						<c:if test="${not empty preReadDto}">
							<a href="${pageContext.request.contextPath}/qANDa/article.do?${query}&num=${preReadDto.num}">${preReadDto.subject}</a>
						</c:if>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						다음글 :
						<c:if test="${not empty nextReadDto}">
							<a href="${pageContext.request.contextPath}/qANDa/article.do?${query}&num=${nextReadDto.num}">${nextReadDto.subject}</a>
						</c:if>
					</td>
				</tr>
			</tbody>
		</table>
		
		<table class="table">
			<tr>
				<td width="50%">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qANDa/reply.do?num=${dto.num}&page=${page}';">답변</button>
					<c:choose>
						<c:when test="${sessionScope.member.userId==dto.userId}">
							<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qANDa/update.do?num=${dto.num}&page=${page}';">수정</button>
						</c:when>
						<c:otherwise>
							<button type="button" class="btn" disabled="disabled">수정</button>
						</c:otherwise>
					</c:choose>
			    	
					<c:choose>
			    		<c:when test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
			    			<button type="button" class="btn" onclick="deleteqANDa();">삭제</button>
			    		</c:when>
			    		<c:otherwise>
			    			<button type="button" class="btn" disabled="disabled">삭제</button>
			    		</c:otherwise>
			    	</c:choose>
				</td>
				<td align="right">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/qANDa/list.do?${query}';">리스트</button>
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