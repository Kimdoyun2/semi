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

.btnSendOsLike {
	background: #abb6cc;
}
</style>

<script type="text/javascript">
<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
function deleteOpensource() {
	if(confirm('게시글을 삭제 하시겠습니까?')){
		let query = "num=${dto.num}&${query}";
		let url = "${pageContext.request.contextPath}/opensource/delete.do?"
		location.href = url + query;
	}
}
</c:if>
</script>

<script type="text/javascript">
function login() {
	location.href="${pageContext.request.contextPath}/member/login.do";
}

function ajaxFun(url, method, query, dataType, fn) {
	$.ajax({
		type:method,
		url:url,
		data:query,
		dataType:dataType,
		success:function(data) {
			fn(data);
		},
		beforeSend:function(jqXHR) {
			jqXHR.setRequestHeader("AJAX", true);
		},
		error:function(jqXHR) {
			if(jqXHR.status === 403) {
				login();
				return false;
			} else if(jqXHR.status === 400) {
				alert("요청 처리가 실패했습니다.");
				return false;
			}
			
			console.log(jqXHR.responseText);
		}
	});
}

// 게시글 공감 여부
$(function() {
	$(".btnSendOsLike").click(function() {
		const $i = $(this).find("i");
		let isNoLike = $i.css("color") == "rgb(255, 255, 255)";
		let msg = isNoLike ? "게시글에 좋아요를 누르시겠습니까? " : "게시글에 좋아요를 취소하시겠습니까? ";
		
		if(! confirm(msg)) {
			return false;
		}
		
		let url = "${pageContext.request.contextPath}/opensource/insertOsLike.do";
		let num = "${dto.num}";
		let query = "num="+num+"&isNoLike="+isNoLike;
		
		const fn = function(data) {
			let state = data.state;
			if(state === "true") {
				let color = "white";
				if(isNoLike) {
					color = "red";
				}
				$i.css("color", color);
				
				let count = data.osLikeCount;
				$("#osLikeCount").text(count);
			} else if(state === "liked") {
				alert("좋아요는 한번만 가능합니다.");
			}
		};
		
		ajaxFun(url, "post", query, "json", fn);
	});
});
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
						${dto.reg_date } | 조회 ${dto.hitCount }
					</td>
				</tr>
				
				<tr style="border-bottom: none;">
					<td colspan="2" valign="top" height="200">
						${dto.content }
					</td>
				</tr>
				
				<tr>
					<td colspan="2" align="center" style="padding-bottom: 20px;">
						<button type="button" class="btn btnSendOsLike" title="좋아요"><i class="fa-solid fa-heart" style="color: ${isUserLike?'red':'white'}"></i>&nbsp;&nbsp;<span id="osLikeCount">${dto.likeCount}</span></button>
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