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
.ar { color: #222; text-decoration: none; cursor: pointer; }
.ar:active, .ar:hover { color: #243b73; text-decoration: underline; }
</style>
</head>
<body>

<header>
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
    <div class="body-container" style="display: flex;">
    	<div class="mainSide" style="float: left;">
    		<div class="rank">
				<span class="rank-title">조회수 랭킹</span>
	    		<table class="rank-table" style="table-layout: fixed; display: table;">
		    		<c:forEach var="dto" items="${rankHitCount}">
					<tr>
						<td style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
							<a class="ar" href="${pageContext.request.contextPath}/${dto.boardName}/article.do?num=${dto.num}&page=1">${dto.listNum}. ${dto.subject}</a>
						</td>
					</tr>
					</c:forEach>
	    		</table>
    		</div>
    		<div class="rank" style="margin-top: 20px;">
    			<span class="rank-title">최근 게시물</span>
	    		<table class="rank-table" style="table-layout: fixed; display: table;">
	    			<c:forEach var="dto" items="${rankReg_date}">
					<tr>
						<td style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
							<a class="ar" href="${pageContext.request.contextPath}/${dto.boardName}/article.do?num=${dto.num}&page=1">${dto.listNum}. ${dto.subject}</a>
						</td>
					</tr>
					</c:forEach>
	    		</table>
	    	</div>
    	</div>
    	<div style="float: left;">
        	<img class="mainImage" src="https://images.unsplash.com/photo-1498050108023-c5249f4df085?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=872&q=80">
    	</div>
    </div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

</html>