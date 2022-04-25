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
.grid-box {
	margin-top: 3px; margin-bottom: 5px;
	display: grid;
	/* auto-fill :  남는 공간(빈 트랙)을 그대로 유지, minmax : '최소, 최대 크기'를 정의 */
	grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
	grid-column-gap: 10px;
	grid-row-gap: 10px;
}
.grid-box .item {
	border: 1px solid #DAD9FF; height: 230px; cursor: pointer;
}
.item > img {
  width: 100%; height: 100%; cursor: pointer;
}
</style>

</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="body-container" style="width: 800px;">
		<div class="body-title">
			<h3><i class="far fa-image"></i> 포토 갤러리 </h3>
		</div>
        
		<table class="table">
			<tr>
				<td width="50%">
					${dataCount}개(${page}/${total_page} 페이지)
				</td>
				<td align="right">
					<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/photo/write.do';">사진올리기</button>
				</td>
			</tr>
		</table>
		
		<div class="grid-box">
			<c:forEach var="dto" items="${list}" varStatus="status">
				<div class="item" title="${dto.subject}"
					onclick="location.href='${articleUrl}&num=${dto.num}';">
					<img src="${pageContext.request.contextPath}/uploads/photo/${dto.imageFilename}">
				</div>
			</c:forEach>
		</div>
		
		<div class="page-box">
			${dataCount == 0 ? "등록된 게시물이 없습니다." : paging}
		</div>

	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>