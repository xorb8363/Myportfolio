<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
		<div style="padding: 30px;">
			<!-- 파일 업로드 기능을 구현할 시에는 <form> 태그안에 반드시  enctype="multipart/form-data"를 작성해주어야 하고, 용량이 크기 때문에 method는 반드시 post로 작성해야 합니다. -->
			<form method="post" action="updateEndDocument.do"
				enctype="multipart/form-data">
				<input type="hidden" name="bNum" id="bNum" class="form-control" value="${dto.bNum}"/>
				<div class="row">
					<div class="form-group">
						<label>작성자</label> <input type="text" name="bWriter" id="bWriter"
							class="form-control" placeholder="작성자를 입력하세요"  value="${dto.bWriter}"/>
					</div>
				</div>
				<div class="row">
					<div class="form-group">
						<label>제목</label> <input type="text" name="bTitle" id="bTitle"
							class="form-control" placeholder="제목을 입력하세요"  value="${dto.bTitle}" />
					</div>
				</div>
				<div class="row">
					<div class="form-group">
						<label>내용</label>
						<textarea name="bContents" id="bContents" class="form-control"
							rows="15" placeholder="내용을 입력하세요">${dto.bContents}</textarea>
					</div>
				</div>
				<div class="row">
					<div class="form-group">
						<label>이미지업로드</label> <input class="form-control"
							id="files-upload" type="file" name="fileName">
					</div>
				</div>
				<div class="row">
					<div class="pull-right">
						<button type="submit" class="btn btn-default"
							id="boardUpdateButton">작성</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</body>

</html>