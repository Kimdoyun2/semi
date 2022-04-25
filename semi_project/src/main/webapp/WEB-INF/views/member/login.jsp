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
main .container {
	margin: 80px auto 50px;
	width: 380px;
	padding: 10px;
	min-height: 200px;
}

.title-body {
	padding: 10px 0; text-align: center;
}

.title-body .article-title {
	font-weight: bold; font-size: 27px; cursor: pointer; color: #243b73;
}

.form-body {
	text-align: center;
}

.form-body .lbl {
	position: absolute; margin-left: 15px; margin-top: 15px; color: #999;
}

.form-body .inputTF {
	width: 100%;
	height: 45px;
	padding: 5px;
	padding-left: 15px;
	border:1px solid #dee4f2;
	background: #f1f3f7;
}

.table input[type=text]:focus, .table input[type=password]:focus {
	border:1px solid #243b73;
}

.msg-box {
	text-align: center; color: blue;
}
</style>

<script type="text/javascript">
function bgLabel(obj, id) {
	if( ! obj.value ) {
		document.getElementById(id).style.display="";
	} else {
		document.getElementById(id).style.display="none";
	}
}

function inputsFocus( id ) {
	// 객체를 보이지 않게 숨긴다.
	document.getElementById(id).style.display="none";
}

function sendLogin() {
    const f = document.loginForm;

	let str = f.userId.value;
    if(!str) {
        alert("아이디를 입력하세요. ");
        f.userId.focus();
        return;
    }

    str = f.userPwd.value;
    if(!str) {
        alert("패스워드를 입력하세요. ");
        f.userPwd.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/member/login_ok.do";
    f.submit();
}
</script>

</head>
<body>

<header>
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container">
		<div class="title-body">
			<span class="article-title">회원 로그인</span>
		</div>
	
		<div class="form-body">
			<form name="loginForm" method="post" action="">
				<table class="table">
					<tr align="center"> 
						<td> 
							<label for="userId" id="lblUserId" class="lbl">아이디</label>
							<input type="text" name="userId" id="userId" class="inputTF" maxlength="15"
								tabindex="1"
								onfocus="inputsFocus('lblUserId');"
								onblur="bgLabel(this, 'lblUserId');">
						</td>
					</tr>
					<tr align="center"> 
					    <td>
							<label for="userPwd" id="lblUserPwd" class="lbl">패스워드</label>
							<input type="password" name="userPwd" id="userPwd" class="inputTF" maxlength="20" 
								tabindex="2"
								onfocus="inputsFocus('lblUserPwd');"
								onblur="bgLabel(this, 'lblUserPwd');">
					    </td>
					</tr>
					<tr align="center"> 
					    <td>
							<button type="button" onclick="sendLogin();" class="btnConfirm">로그인</button>
					    </td>
					</tr>
					<tr align="center">
					    <td>
							<a class="a3" href="${pageContext.request.contextPath}">아이디 찾기</a>&nbsp;&nbsp; 
							<a class="a3" href="${pageContext.request.contextPath}">비밀번호 찾기</a>&nbsp;&nbsp;
							<a class="a3" href="${pageContext.request.contextPath}/member/member.do">회원가입</a>
					    </td>
					</tr>
				</table>
				
				<table class="table">
					<tr>
						<td class="msg-box">${message}</td>
					</tr>
				</table>
			</form>           
		</div>
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

</body>
</html>