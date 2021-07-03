<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="star.important.utils.DataTypeUtil"%>
<%@page import="star.important.utils.NumberUtil"%>
<%@page import="org.springframework.util.unit.DataUnit"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang3.*"%>
<%@page import="star.important.utils.*"%>
<%@include file="../common/public.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>上报情况</title>
		<%@include file="../common/head.jsp"%>
		<script src="${ctx}/js/monthReport/reportPreview.js?version=${version}" type="text/javascript"/>
		<script type="text/javascript" src="${basePath}plugins/My97DatePicker/WdatePicker.js?version=${version}"></script>
		<style type="text/css">
		    
			.inputTable td {
				vertical-align:middle;
				text-align: center;
			}
			.inputTable td.titleText {
				color: #333333;
				background:#f4f8f9;
			}
			.inputTable td input.formText {
				width:90%; 
			}
			.inputTable td input.Wdate{
				width:80px; 
			}
		</style>
	</head>
	<body>
		<input type="hidden" name="year" id="year" value=""/>
		<input type="hidden" name="month" id="month" value=""/>
		<div class="doArea mt10" >
			<form action="reportPreview.jsp" method="post" id="searchform" class="form-inline" style="text-align: center;">
			   <input type="hidden" name="projectId" id="projectId" value="${projectId}"/>
			   <div class="form-group">
                    <select id="yearSel" name="yearSel"  class="form-control"  style="width: 80px;">
                    </select>
               </div>
               <div class="form-group">
                     <select id="monthSel" name="monthSel"  class="form-control" style="width: 80px;" >
                     </select>
               </div>
               <div class="form-group">
					<button class="formButton btn btn-success" type="button"  id="search">查 询</button>
               </div>
                <div class="form-group">
					<button class="formButton btn btn-success" type="button"  id="export">导 出</button>
               </div>
			</form>    
		</div>
		<div class="input">
			<table class="listTitle">
				<tbody>
					<tr>
						<td class="left"></td>
						<td class="center" id="reportPreviewCenter">
						</td>
						<td class="right"></td>
					</tr>
				</tbody>
			</table>
			<table border="0" class="inputTable">
		        <tr>
		            <td rowspan="1" colspan="1">项目名称</td>
		            <td rowspan="1" colspan="1">进度类别</td>
		            <td rowspan="1" colspan="1">建设规模及建设内容</td>
		            <td rowspan="1" colspan="1">建设起止日期</td>
		            <td rowspan="1" colspan="1">总投资</td>
		            <td rowspan="1" colspan="1">止上年底完成投资</td>
		            <td rowspan="1" colspan="1">年度投资目标</td>
		            <td rowspan="1" colspan="1">当月完成投资</td>
		            <td rowspan="1" colspan="1">1到当月完成投资</td>
		            <td rowspan="1" colspan="1">占年度目标的%</td>
		        </tr>
		        <tr>
		            <td rowspan="4" colspan="1" id="projectName"></td>
		            <td rowspan="4" colspan="1" id="schedule"></td>
		            <td rowspan="4" colspan="1" id="projectMemo"></td>
		            <td rowspan="1" colspan="1" id="planStartStr"></td>
		            <td rowspan="1" colspan="1" id="projectInvest"></td>
		            <td rowspan="1" colspan="1" id="lastYearPlanComplete"></td>
		            <td rowspan="1" colspan="1" id="target4Fund"></td>
		            <td rowspan="1" colspan="1" id="monthCompleteFunds"></td>
		            <td rowspan="1" colspan="1" id="monthCompleteFundsAdd"></td>
		            <td rowspan="1" colspan="1" id="monthCompleteFundsAddPercent"></td>
		        </tr>
		        <tr>
		            <td rowspan="1" colspan="5">资金到位情况（万元）</td>
		            <td rowspan="1" colspan="1">所在省辖市、县区</td>
		            <td rowspan="1" colspan="1">责任单位</td>
		        </tr>
		        <tr> 
		        	<td rowspan="1" colspan="1"></td>
		        	<td rowspan="1" colspan="1">政府资金</td>
		            <td rowspan="1" colspan="1">银行贷款</td>
		            <td rowspan="1" colspan="1">自筹资金</td>
		            <td rowspan="1" colspan="1">其他资金</td>
		            <td rowspan="2" colspan="1" id="projectCity"></td>
		            <td rowspan="2" colspan="1" id="projectUnit"></td>
		        </tr>
		        <tr>
		        	<td rowspan="1" colspan="1">总计</td>
		            <td rowspan="1" colspan="1" id="fundTotal"></td>
		            <td rowspan="1" colspan="1" id="sourceInternal"></td>
		            <td rowspan="1" colspan="1" id="sourceSelf"></td>
		            <td rowspan="1" colspan="1" id="sourceOther"></td>
		        </tr>
		        <tr>
		        	<td rowspan="1" colspan="3">土地审批情况（亩）</td>
		            <td rowspan="1" colspan="1">年度计划到位</td>
		            <td rowspan="1" colspan="1" id="fundYearTotal"></td>
		            <td rowspan="1" colspan="1" id="fundYearHome"></td>
		            <td rowspan="1" colspan="1" id="fundYearSelf"></td>
		            <td rowspan="1" colspan="1" id="fundYearOther"></td>
		            <td rowspan="1" colspan="2">业主单位</td>
		        </tr>
		        <tr>
		            <td rowspan="1" colspan="1">总需求</td>
		            <td rowspan="1" colspan="1">年度需求</td>
		            <td rowspan="1" colspan="1">1到当月已解决</td>
		            <td rowspan="1" colspan="1">当月到位</td>
		            <td rowspan="1" colspan="1" id="financialFunds"></td>
		            <td rowspan="1" colspan="1" id="bankLoans"></td>
		            <td rowspan="1" colspan="1" id="selfFonds"></td>
		            <td rowspan="1" colspan="1" id="otherFunds"></td>
		            <td rowspan="2" colspan="2" id="projectOwner"></td>
		        </tr>
		        <tr>
		            <td rowspan="1" colspan="1" id="planGround"></td>
		            <td rowspan="1" colspan="1" id="planThen"></td>
		            <td rowspan="1" colspan="1" id="solveLand"></td>
		            <td rowspan="1" colspan="1">1到当月到位</td>
		            <td rowspan="1" colspan="1" id="financialFundsAdd"></td>
		            <td rowspan="1" colspan="1" id="bankLoansAdd"></td>
		            <td rowspan="1" colspan="1" id="selfFondsAdd"></td>
		            <td rowspan="1" colspan="1" id="otherFundsAdd"></td>
		        </tr>
		        <tr>
		            <td rowspan="1" colspan="10">形象进度情况</td>
		        </tr>
		        <tr>
		        	<td rowspan="1" colspan="3">前期工作进展情况</td>
					<td rowspan="1" colspan="1">当月参建人数</td>
		            <td rowspan="1" colspan="4">工程建设进展情况</td>
		            <td rowspan="1" colspan="2">存在的主要问题</td>
		        </tr>
		        <tr>
		        	<td rowspan="1" colspan="3" style="text-align: left;" id="beforeStr"></td>
		        	<td rowspan="1" colspan="1" id="joinPeoplesStr"></td>
		            <td rowspan="1" colspan="4" id="scheduleStr"></td>
		            <td rowspan="1" colspan="2" id="troubleStr"></td>
		        </tr>
			</table>
			<div class="buttonArea">
				<input type="button" class="formButton btn btn-success" value="返 回" id="back"/>
			</div>
		</div>
	</body>
</html>
