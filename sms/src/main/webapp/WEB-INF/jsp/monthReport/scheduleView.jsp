<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>进度信息</title>
		<%@include file="../common/head.jsp"%>
		<style type="text/css">
			.inputTable th {width:120px;}
		</style>
		<script src="${ctx}js/monthReport/scheduleView.js?version=${version}" type="text/javascript"></script>
		<script type="text/javascript">
			$(document).ready(function() {
				let isParent = '${project.isParent}';
				if(isParent=='1'){
					$("#joinPeoples").attr("disabled","disabled");
				}
			});
		</script>
	</head>
	<body>
		<input type="hidden" name="update" id="update" value=""/>
		<div style="text-align: center; font-weight: 600;padding-top: 8px;" id="showImg">
		</div>
		<div class="input">
			<table class="listTitle">
				<tbody>
					<tr>
						<td class="left"></td>
						<td class="center" id="tableTitleName">
						</td>
						<td class="right"></td>
					</tr>
				</tbody>
			</table>
			<form name="dataForm" id="dataForm" method="post"
				action=<%=response.encodeURL("schedule.jsp")%>>
				<input type="hidden" name="type" id="type" value="" />
				<input type="hidden" name="reportId" id="reportId" value=""/>
				<div class="doArea mb5" style="text-align: center; font-weight: 600;margin-bottom: 0px;">
	            	项目参考模板
				</div>
				<div class="doArea mb5"
					style="text-align: center; font-weight: 600; margin-bottom: 0px;">
					<div align="left">当月总参建人数（含管理） </div>
				</div>
				<table border="0" class="inputTable">
					<tr>
						<td>
							参建人数
							<input class="formText projectText"  id="joinPeoples" name="joinPeoples" type="text"  style="width: 92%;"  maxlength="10" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" />
						</td>
					</tr>
				</table>
				<div class="doArea mb5"
					style="text-align: center; font-weight: 600; margin-bottom: 0px;">
					<div align="left">形象进度<span class="toptip">(请填写从项目开工到目前的累计工程形象进度，总字数不得超过200字，此栏必须填写，不能为空;（l）未开工和前期项目，填报前期工作开展情况。应开末开的增报原因；（2）续建停工无进展项目，填报停工加原因。)</span> </div>
				</div>
				<table border="0" class="inputTable">
					<tr>
						<td>
							填写内容
							<textarea class="formTextarea" rows="5" style="width: 92%;" name="schedule" id="schedule"></textarea>
						</td>
					</tr>
				</table>
				<div class="doArea mb5"
					style="text-align: center; font-weight: 600; margin-bottom: 0px;">
					<div align="left">存在主要问题<span class="toptip">(只反映需要省、市和省直管县（市）协调解决的事项，如建设环境、资金等问题，总字数不得超过50字。)</span></div>
				</div>
				<table border="0" class="inputTable">
					<tr>
						<td>
							主要问题
							<textarea class="formTextarea" rows="5" style="width: 92%;" name="trouble" id="trouble"></textarea>
						</td>
					</tr>
				</table>
			</form>
			<div class="buttonArea">
				<input type="button" class="formButton  btn  btn-success"  value="操作"  id="save" />
				&nbsp;&nbsp;
				<input type="button" class="formButton  btn  btn-success" value="返 回" id="back" />
			</div>
		</div>
	</body>
</html>
