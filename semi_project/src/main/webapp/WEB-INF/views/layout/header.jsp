<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="header-top">
    <div class="header-left">
        <h1 class="logo"><a href="${pageContext.request.contextPath}/">DOKKY</a></h1>
    </div>
    <div class="header-right">
        <div style="text-align: right;">
        	<c:if test="${empty sessionScope.member}">
				<a class="a2" href="${pageContext.request.contextPath}/member/login.do">로그인</a>
					&nbsp;&nbsp;
				<a class="a2" href="${pageContext.request.contextPath}/member/member.do">회원가입</a>
			</c:if>
			<c:if test="${not empty sessionScope.member}">
				<span style="color:#243b73;">${sessionScope.member.userName}</span>님
					&nbsp;&nbsp;
				<a class="a2" href="${pageContext.request.contextPath}/member/logout.do">로그아웃</a>
			</c:if>
        </div>
    </div>
</div>

<div class="menu">
    <ul class="nav">
        <li>
            <a href="${pageContext.request.contextPath}/lecture/list.do">강의</a>
        </li>
			
        <li>
            <a href="#">Q&amp;A</a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/opensource/list.do">오픈소스</a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/notice/list.do">자유게시판</a>
        </li>
        
        <li>
            <a href="#">스터디모집</a>
        </li>

        <c:if test="${not empty sessionScope.member}">
        <li style="float: right;">
            <a href="${pageContext.request.contextPath}/mypage/mypage.do">마이페이지</a>
        </li>
        </c:if>
    </ul>      
</div>

<div class="navigation">
</div>