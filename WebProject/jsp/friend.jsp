<%@page import="model.*"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<style>
div.container {
	width: 700px;
	border:1px solid lightGary;
	position:relative;
	margin:auto;
	font-family:"나눔스퀘어";
}
div.container table.friendsList{
	position:relative;
	width:500px;
	margin-right:200px;
}
div.container table.friendsList td.memberImg{
	width:100px;
}
div.container table.friendsList td.memberInfo{
	width:300px;
	font-size:17px;
}
div.container table.friendsList td.addFriend{
	width:100px;
}
div.container table.friendsList td.memberImg img{
	width:70px;
	height:70px;
	border-radius:70px;
}

div.container table.friendsList td.addFriend input.addFriend{
	width:30px;
	height:30px;
	border-radius:70px;
	background-image: url('../img/add.png');
	background-size: 30px 30px;
	border:0;
}
div.container table.friendsList td.addFriend input:hover{
	cursor:pointer;
}
</style>
<body>
<%ArrayList<Member> result = (ArrayList<Member>)session.getAttribute("result");
Member user = (Member)session.getAttribute("member"); %>

	
	<body>
			
			<% for(int i=0; i<result.size(); i++) { %>
			<form method="post" action="#">
				<div class="container">
					<div class="face"><a href=""><img src="/WebProject/img/user.PNG"/></a></div>
					<div class="info"><br><a href=""><font size="4"><%= result.get(i).getName() %></font></a><br>
    					<font size="3" color="black"><%= result.get(i).getDepartment() %></font></div>
    				<div class="check"><div style= "position: relative; top: 35%;">
    				<input type="submit" name="yes" value="친구 추가"></div></div>
    			</div>
    	   <div class="blank"></div>
    	   <hr>
    	   <div class="blank"></div>
    	   </form>
			<% } %>
	




</body>
</html>