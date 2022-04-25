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
	    		<table class="rank-table">
	    			<tr>
	    				<td>1. [html 강의] 웹프로...</td>
	    			</tr>
	    			<tr>
	    				<td>2. [java 강의] java...</td>
	    			</tr>
	    			<tr>
	    				<td>3. [DB 강의] 데이터...</td>
	    			</tr>
	    			<tr>
	    				<td>4. [공유] 요즘 취업...</td>
	    			</tr>
	    			<tr>
	    				<td>5. [자유게시판] 국비...</td>
	    			</tr>
	    		</table>
    		</div>
    		<div class="rank" style="margin-top: 20px;">
    			<span class="rank-title">최근 게시물</span>
	    		<table class="rank-table">
	    			<tr>
	    				<td>1. [html 강의] 웹프로...</td>
	    			</tr>
	    			<tr>
	    				<td>2. [java 강의] java...</td>
	    			</tr>
	    			<tr>
	    				<td>3. [DB 강의] 데이터...</td>
	    			</tr>
	    			<tr>
	    				<td>4. [공유] 요즘 취업...</td>
	    			</tr>
	    			<tr>
	    				<td>5. [자유게시판] 국비...</td>
	    			</tr>
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