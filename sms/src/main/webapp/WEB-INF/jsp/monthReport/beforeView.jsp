<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="star.important.utils.*"%>
<%@include file="../common/public.jsp"%>

<%--<%
//得到项目id
Object projectIdObj=session.getAttribute("projectId");
int projectId=0;
if(projectIdObj!=null){
	projectId=(Integer)projectIdObj;
}
//得到报表id
Object reportIdObj=session.getAttribute("reportId");
int reportId=0;
if(reportIdObj!=null){
	reportId=(Integer)reportIdObj;
}
//得到进度id
Object procedureIdObj=session.getAttribute("procedureId");
int procedureId=0;
if(procedureIdObj!=null){
	procedureId=(Integer)procedureIdObj;
}
//得到类型
String typeObj=request.getParameter("type");
int type=2;
if(typeObj!=null&&!typeObj.equals("")){
	type=Integer.valueOf(typeObj);
}
String operation="修改";
if(type==1){
	operation="修改";
}else{
	operation="下一步";
}
%>--%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>上报情况</title>
		<%@include file="../common/head.jsp"%>
		<style type="text/css">
			.inputTable th {
				width: 160px;
			}
		</style>
		<script src="${ctx}/js/monthReport/beforeView.js?version=${version}" type="text/javascript"></script>
	</head>
	<body>
	<input type="hidden" name="newLandFirst" id="newLandFirst" value="" />
	<input type="hidden" name="nowLandStepFirst" id="nowLandStepFirst" value="" />
	<input type="hidden" name="paperStepFirst" id="paperStepFirst" value="" />
		<div style="text-align: center; font-weight: 600;padding-top: 8px;" id="showImg">
		</div>
		<div class="input">
			<table class="listTitle">
				<tbody>
					<tr>
						<td class="left"></td>
						<td class="center" id="beforeViewTitle">

						</td>
						<td class="right"></td>
					</tr>
				</tbody>
			</table>
			<form name="dataForm" id="dataForm" method="post" action="">
				
				<input type="hidden" name="type" id="type" value="" />
				
				<input type="hidden" name="projectApprovalStepSelect" id="projectApprovalStepSelect" value="" />
				<input type="hidden" name="projectApprovalStepSubSelect" id="projectApprovalStepSubSelect" value="" />
				<input type="hidden" name="projectApprovalStepFuture" id="projectApprovalStepFuture" value="" />
				<input type="hidden" name="projectApprovalStepEnd" id="projectApprovalStepEnd" value="" />
				<input type="hidden" name="projectApprovalStepCaption" id="projectApprovalStepCaption" value="" />
				
				<input type="hidden" name="planApprovalStepSelect" id="planApprovalStepSelect" value="" />
				<input type="hidden" name="planApprovalStepSubSelect" id="planApprovalStepSubSelect" value="" />
				<input type="hidden" name="planApprovalStepFuture" id="planApprovalStepFuture" value="" />
				<input type="hidden" name="planApprovalStepEnd" id="planApprovalStepEnd" value="" />
				<input type="hidden" name="planApprovalStepCaption" id="planApprovalStepCaption" value="" />
				
				<input type="hidden" name="eiaApprovalStepSelect" id="eiaApprovalStepSelect" value="" />
				<input type="hidden" name="eiaApprovalStepSubSelect" id="eiaApprovalStepSubSelect" value="" />
				<input type="hidden" name="eiaApprovalStepFuture" id="eiaApprovalStepFuture" value="" />
				<input type="hidden" name="eiaApprovalStepEnd" id="eiaApprovalStepEnd" value="" />
				<input type="hidden" name="eiaApprovalStepCaption" id="eiaApprovalStepCaption" value="" />
				<div class="doArea mb5" style="text-align: center; font-weight: 600; margin-bottom: 0px;">
					项目情况
					<label class="radio">
						<input type="radio" name="projectApproval" value="3" checked="checked"/>备案
					</label>
					<label class="radio">
						<input type="radio" name="projectApproval" value="2"/>核准
					</label>
					<label class="radio">
						<input type="radio" name="projectApproval" value="1"/>审批
					</label>
				</div>
				<table border="0" class="inputTable" id="projectApprovalType1" style="display: none;">
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="1" checked="checked"/>
								未开始办理
							</label>
						</th>
						<td></td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="2"/>
								项目建议书(立项)
							</label>
						</th>
						<td>
							<p>
								<input type="radio" name="projectApprovalStepSub2" value="1"/>已报
								<select class="areaSelect" name="projectApprovalStepFuture2" id="projectApprovalStepFuture2">
									<option value="1" selected="selected" >
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门），待审批
							</p>
							<p>
								<input type="radio" name="projectApprovalStepSub2" value="2"/>已经
								<select class="areaSelect" name="projectApprovalStepEnd2" id="projectApprovalStepEnd2">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门）审批，文号
								<input type="text" class="formText projectText" maxlength="200" name="projectApprovalStepCaption2" id="projectApprovalStepCaption2"/>
							</p>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" id="" value="3"/>
								可行性研究报告
							</label>
						</th>
						<td>
							<p>
								<input type="radio" name="projectApprovalStepSub3" value="1"/>已报
								<select class="areaSelect" name="projectApprovalStepFuture3" id="projectApprovalStepFuture3">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门），待审批
							</p>
							<p>
								<input type="radio" name="projectApprovalStepSub3" value="2"/>已经
								<select class="areaSelect" name="projectApprovalStepEnd3" id="projectApprovalStepEnd3">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门）审批，文号
								<input type="text" class="formText projectText" maxlength="200" name="projectApprovalStepCaption3" id="projectApprovalStepCaption3"/>
							</p>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="4"/>
								初步设计
							</label>
						</th>
						<td>
							<p>
								<input type="radio" name="projectApprovalStepSub4" value="1"/>已报
								<select class="areaSelect" name="projectApprovalStepFuture4" id="projectApprovalStepFuture4">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门），待审批
							</p>
							<p>
								<input type="radio" name="projectApprovalStepSub4" value="2"/>已经
								<select class="areaSelect" name="projectApprovalStepEnd4" id="projectApprovalStepEnd4">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门）审批，文号
								<input type="text" class="formText projectText" maxlength="200" name="projectApprovalStepCaption4" id="projectApprovalStepCaption4"/>
							</p>
						</td>
					</tr>	
					</table>
					<table border="0" class="inputTable" id="projectApprovalType2" style="display: none;">				
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="5" checked="checked"/>
								未开始办理
							</label>
						</th>
						<td></td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="6" />
								正在编制项目申请报告
							</label>
						</th>
						<td></td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="7"/>
								项目申请报告
							</label>
						</th>
						<td>
							<p>
								<input type="radio" name="projectApprovalStepSub7" value="1"/>已报
								<select class="areaSelect" name="projectApprovalStepFuture7" id="projectApprovalStepFuture7">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门），待核准
							</p>
							<p>
								<input type="radio" name="projectApprovalStepSub7" value="2"/>已经
								<select class="areaSelect" name="projectApprovalStepEnd7" id="projectApprovalStepEnd7">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门）核准，文号
								<input type="text" class="formText projectText" maxlength="200" name="projectApprovalStepCaption7" id="projectApprovalStepCaption7"/>
							</p>
						</td>
					</tr>	
					</table>
					<table border="0" class="inputTable" id="projectApprovalType3" >
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="8" checked="checked"/>
								未开始办理
							</label>
						</th>
						<td></td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="projectApprovalStep" value="9"/>
								项目备案申请表
							</label>
						</th>
						<td>
							<p>
								<input type="radio" name="projectApprovalStepSub9" value="1"/>已报
								<select class="areaSelect" name="projectApprovalStepFuture9" id="projectApprovalStepFuture9">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门），待备案
							</p>
							<p>
								<input type="radio" name="projectApprovalStepSub9" value="2"/>已经
								<select class="areaSelect" name="projectApprovalStepEnd9" id="projectApprovalStepEnd9">
									<option value="1" selected="selected">
										省
									</option>
									<option value="2">
										市
									</option>
									<option value="3">
										县
									</option>
									<option value="4">
										国家
									</option>
								</select>
								发改委（或行业主管部门）备案，文号
								<input type="text" class="formText projectText" maxlength="200" name="projectApprovalStepCaption9" id="projectApprovalStepCaption9"/>
							</p>
						</td>
					</tr>	
				</table>
				<div class="doArea mb5"
					style="text-align: center; font-weight: 600; margin-bottom: 0px;">
					建设规划审批情况
				</div>
				<table border="0" class="inputTable">
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="planApprovalStep" value="1" checked="checked" />
								未开始办理
							</label>
						</th>
						<td>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="planApprovalStep" value="2"/>
								正在选址
							</label>
						</th>
						<td>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="planApprovalStep" value="3"/>
								控制性规划已经
							</label>
						</th>
						<td>
							<p>
							<input type="radio" name="planApprovalStepSub3" value="1"/>已报
							<select class="areaSelect" name="planApprovalStepFuture3" id="planApprovalStepFuture3">
								<option value="1">
									省
								</option>
								<option value="2">
									市
								</option>
								<option value="3">
									县
								</option>
								<option value="4">
									国家
								</option>
							</select>
							待审批
							</p>
							<p>
							<input type="radio" name="planApprovalStepSub3" value="2"/>已经
							<select class="areaSelect" name="planApprovalStepEnd3" id="planApprovalStepEnd3">
								<option value="1">
									省
								</option>
								<option value="2">
									市
								</option>
								<option value="3">
									县
								</option>
								<option value="4">
									国家
								</option>
							</select>
							审批，文号
							<input type="text" class="formText projectText" maxlength="200" name="planApprovalStepCaption3" id="planApprovalStepCaption3"/>
							</p>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="planApprovalStep" value="4"/>
								选址意见书
							</label>
						</th>
						<td>
							文号
							<input type="text" class="formText projectText" maxlength="50" name="planApprovalStepCaption4" id="planApprovalStepCaption4"/>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="planApprovalStep" value="5"/>
								设计条件
							</label>
						</th>
						<td>
							文号
							<input type="text" class="formText projectText" maxlength="200" name="planApprovalStepCaption5" id="planApprovalStepCaption5"/>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="planApprovalStep" value="6"/>
								建设用地规划许可证
							</label>
						</th>
						<td>
							文号
							<input type="text" class="formText projectText" maxlength="200" name="planApprovalStepCaption6" id="planApprovalStepCaption6"/>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="planApprovalStep" value="7"/>
								建设工程规划许可证
							</label>
						</th>
						<td>
							文号
							<input type="text" class="formText projectText" maxlength="200" name="planApprovalStepCaption7" id="planApprovalStepCaption7"/>
						</td>
					</tr>
				</table>
				<div class="doArea mb5"
					style="text-align: center; font-weight: 600; margin-bottom: 0px;">
					环评审批情况
				</div>
				<table border="0" class="inputTable">
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="eiaApprovalStep" value="1" checked="checked"/>
								未开始办理
							</label>
						</th>
						<td>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="eiaApprovalStep" value="2"/>
								正在编制环评报告
							</label>
						</th>
						<td>
						</td>
					</tr>
					<tr>
						<th>
							<label class="radio">
								<input type="radio" name="eiaApprovalStep" value="3"/>
								环评报告
							</label>
						</th>
						<td>
							<p>
							<input type="radio" name="eiaApprovalStepSub3" value="1"/>已报
							<select class="areaSelect" name="eiaApprovalStepFuture3" id="eiaApprovalStepFuture3">
								<option value="1">
									省
								</option>
								<option value="2">
									市
								</option>
								<option value="3">
									县
								</option>
								<option value="4">
									国家
								</option>
							</select>
							待审批
							</p>
							<p>
							<input type="radio" name="eiaApprovalStepSub3" value="2"/>已经
							<select class="areaSelect" name="eiaApprovalStepEnd3" id="eiaApprovalStepEnd3">
								<option value="1">
									省
								</option>
								<option value="2">
									市
								</option>
								<option value="3">
									县
								</option>
								<option value="4">
									国家
								</option>
							</select>
							审批，文号
							<input type="text" class="formText projectText" maxlength="200" name="eiaApprovalStepCaption3" id="eiaApprovalStepCaption3"/>
							</p>
						</td>
					</tr>
				</table>
				<div class="doArea mb5"
					style="text-align: center; font-weight: 600; margin-bottom: 0px;">
					用地审批情况<span class="toptip">（没有填“0”）</span>
				</div>
				<table border="0" class="inputTable">
					<tr>
						<th>总用地</th>
						<td>
							<input type="text" class="formText projectText" maxlength="13" name="totalLand" id="totalLand" value="" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')"/>
							亩
						</td>
					</tr>
					<tr>
						<th>新增用地</th>
						<td>
							<label class="radio">
								<input type="radio" name="newLand" value="1" />有
							</label>
							<label class="radio">
								<input type="radio" name="newLand" value="2" checked="checked"/>无
							</label>
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
							<input type="text" class="formText projectText" maxlength="200" name="lastLandArea" id="lastLandArea" value=""  onkeyup="this.value=this.value.replace(/[^0-9]/g,'')"/>
							亩土地已办理土地证，土地证号
							<input type="text" class="formText projectText" maxlength="200" name="lastLandCaption" id="lastLandCaption"  value="" />
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
							<input type="text" class="formText projectText" maxlength="200" name="nowLandArea" id="nowLandArea" value="" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')"/>
							亩正在办理以下手续：
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
							<input type="radio" name="nowLandStep" value="1" checked="checked"/>土地预审
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
							<input type="radio" name="nowLandStep" value="2" />
							<select class="areaSelect" name="paperStep" id="paperStep">
								<option value="1" selected="selected">
									省
								</option>
								<option value="2">
									市
								</option>
								<option value="3">
									县
								</option>
								<option value="4">
									国家
								</option>
							</select>
							土地组卷，省政府或国务院批复文号
							<input type="text" class="formText projectText" maxlength="200" name="paperCaption" id="paperCaption"  value="" />
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
								<input type="radio" name="nowLandStep" value="3"/>土地征收
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
								<input type="radio" name="nowLandStep" value="4"/>招拍挂、划拨、协议等方式出让
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
								<input type="radio" name="nowLandStep" value="5"/>已签订土地出让合同
						</td>
					</tr>
					<tr class="isnewLand">
						<th></th>
						<td>
								<input type="radio" name="nowLandStep" value="6" />已办理土地证，土地证号
								<input type="text" class="formText projectText" maxlength="50" name="partCaption" id="partCaption" value=""/>
						</td>
					</tr>
				</table>
			</form>
			<div class="buttonArea">	
				<input type="button" class="formButton  btn  btn-success"   value="操作"  id="save" />
				&nbsp;&nbsp;
				<input type="button" class="formButton  btn  btn-success" value="返 回" id="back" />
			</div>
		</div>
	</body>
	
</html>
