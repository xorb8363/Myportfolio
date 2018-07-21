<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<!-- 부트스트랩 관련 -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">

<meta charset="UTF-8">
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<title>Insert title here</title>
</head>
<body>
	<!-- 부트스트랩의 기본 -->
	<div class="container">
		<div class="page-header">
			<h1>게시판</h1>
		</div>
		<table class="table table-hover">
			<thead>
				<tr>
					<th style="width: 10%;">번호</th>
					<th style="width: 55%;">제목</th>
					<th style="width: 10%;">작성자</th>
					<th style="width: 15%;">날짜</th>
					<th style="width: 10%;">조회수</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>${dto.bNum}</td>
					<td>${dto.bTitle}</a></td>
					<td>${dto.bWriter}</td>
					<td><fmt:formatDate value="${dto.bRegDate}"
							pattern="yyyy-MM-dd HH:mm:ss" /></td>
					<td>${dto.bReadCount}</td>
				</tr>
				<tr>
					<td colspan="5"></td>
				</tr>
				<tr>
					<td colspan="5">본문</td>
				</tr>
				<tr>
					<td colspan="5">${dto.bContents}</td>
				</tr>
			</tbody>
		</table>
		<div class="pull-right">
			<a href="updateDocument.do?bNum=${dto.bNum}"><button type="button"
					class="btn btn-default">수정</button></a>
			<a href="deleteDocument.do?bNum=${dto.bNum}"><button type="button"
					class="btn btn-default">삭제</button></a>
			<a href="boardList.do"><button
					type="button" class="btn btn-default">뒤로</button></a>
		</div>
	</div>
</body>

</html>