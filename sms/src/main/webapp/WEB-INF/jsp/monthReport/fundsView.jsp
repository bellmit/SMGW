<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="java.sql.*"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="star.important.domian.*"%>
<%@page import="star.important.utils.*"%>
<%@include file="../common/public.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>上报情况</title>
		<%@include file="../common/head.jsp"%>
		<link href="${ctx}plugins/uploadify/uploadify.css?version=${version}" rel="stylesheet" type="text/css"/>
		<script type="text/javascript" src="${ctx}plugins/My97DatePicker/WdatePicker.js?version=${version}"></script>
		<script type="text/javascript" src="${ctx}plugins/uploadify/jquery.uploadify.min.js?version=${version}"></script>
		<style type="text/css">
			.inputTable th {width:120px;}
		</style>
		<script src="${ctx}js/monthReport/fundsView.js?version=${version}" type="text/javascript"></script>
		<script type="text/javascript">
		$(document).ready(function() {
			let isParent = '${project.isParent}';
			if(isParent=='1'){
				$("#selfFonds,#bankLoans,#financialFunds,#otherFunds,#monthCompleteFunds").attr("disabled","disabled");
			}
			//单击
			$("#save").click(function() {
				submitData();
				return false;
			});
			//单击返回
			$("#back").click(function() {
				window.history.back();
				return false;
			});
			//是否开工
			$(":radio[name='isStart']").click(function(){
				$(this).prop("checked",true);
				if($(this).val()==1){
					$("#startSet").show();
				}else{
					$("#startSet").hide();
				}
			});
			//是否竣工
			$(":radio[name='isEnd']").click(function(){
				if($(this).val()==1){
					$("#endSet").show();
				}else{
					$("#endSet").hide();
				}
			});
			//////////////////////////////////////////////////////////////////////////////////
			//上传资金到位附件
			function uploadFundsFile(){
			    return {
			        //和存放队列的DIV的id一致
			        'queueID':'queueFundsFile',
			        //服务器端脚本使用的文件对象的名称 $_FILES个['upload']
			        'fileObjName':'uploadFundsFile',
			        //上传处理程序
			        'uploader':'fileUploadFunds.jsp',
			        //按钮文字
			        'buttonText' : '上传附件...',
			        //浏览按钮的背景图片路径uploadfile.png
			        'buttonImage':'../plugins/uploadify/upload-file.png',
			        //取消上传文件的按钮图片，就是个叉叉
			        'cancel':'../plugins/uploadify/upload-cancel.png',
			        //浏览按钮的宽度
			        'width':'75',
			        //浏览按钮的高度
			        'height':'26',
			        //在浏览窗口底部的文件类型下拉菜单中显示的文本
			        'fileTypeDesc':'支持的格式',
			        //允许上传的文件后缀
			        'fileTypeExts':"*.gif;*.jpg;*.jpeg;*.png;*.doc;*.docx",
			        //上传文件的大小限制
			        'fileSizeLimit':"10MB",
			        //上传数量
			        'queueSizeLimit' : 25,
			        //开启调试
			        'debug' : false,
			        //是否自动上传
			        'auto':true,
			        //上传后是否删除
			        'removeComplete':false,
			        //清除
			        removeTimeout : 0,     
			        //超时时间
			        'successTimeout':99999,
			        //flash
			        'swf': '../plugins/uploadify/uploadify.swf',
			        //不执行默认的onSelect事件
			        'overrideEvents' : ['onDialogClose'],
			        //每次更新上载的文件的进展
			        'onUploadProgress' : function(file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal) {
			            //有时候上传进度什么想自己个性化控制，可以利用这个方法
			        },
			        //选择上传文件后调用
			        'onSelect' : function(file) {
			            //uploadBefore.text(file.name);
			            return true;
			        },
			        //返回一个错误，选择文件的时候触发
			        'onSelectError':function(file, errorCode, errorMsg){
			            switch(errorCode) {
			                case -100:
			                    alert("上传的文件数量已经超出系统限制的"+$('#uploadFundsFile').uploadify('settings','queueSizeLimit')+"个文件！");
			                    break;
			                case -110:
			                	alert("文件 ["+file.name+"] 大小超出系统限制,最大允许2M！");
			                    break;
			                case -120:
			                    alert("文件 ["+file.name+"] 大小异常！");
			                    break;
			                case -130:
			                    alert("文件 ["+file.name+"] 类型不正确！");
			                    break;
			            }
			        },
			        //检测FLASH失败调用
			        'onFallback':function(){
			            alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
			        },
			        //上传到服务器，服务器返回相应信息到data里
			        'onUploadSuccess':function(file, data, response){
			        	//得到上传返回值
			        	var fileObj=jQuery.parseJSON(data);
			        	var $image=$("<p id='"+fileObj.fileId+"'><span>"+fileObj.fileName+"</span><span class='pl15 blue pointer'><a href='../"+fileObj.filePath+"'  target= '_blank'>下载</a></span><span class='pl15 blue pointer' fileId='"+fileObj.fileId+"'>删除</span></p>");
			        	$("#fundsFileView").append($image);
			        	alert("上传成功！");
			        },
			        //上传前取消文件
			        'onCancel' : function(file) {
			        }
			    };
			};
			//上传项目图片
			$("#uploadFundsFile").uploadify(uploadFundsFile());
			//删除文件
			function deleteFile(fileId){
				
				var paramData={
						"dataId":fileId
				};
			    $.ajax({
			        url:"delete.jsp",
			        type:"post",
			        dataType:'text',
			        data:paramData,
			        success:function (data) {
			        	$("#"+fileId).remove();
			     	}
			    });
			};
			//删除文件
			$("#fundsFileView").delegate("span","click",function(){
				var currentFileId = $(this).attr("fileId");
				deleteFile(currentFileId);
			});
			//////////////////////////////////////////////////////////////////////////////////
			//上传资金完成附件
			function uploadCompleteFile(){
			    return {
			        //和存放队列的DIV的id一致
			        'queueID':'queueCompleteFile',
			        //服务器端脚本使用的文件对象的名称 $_FILES个['upload']
			        'fileObjName':'uploadCompleteFile',
			        //上传处理程序
			        'uploader':'fileUploadComplete.jsp',
			        //按钮文字
			        'buttonText' : '上传附件...',
			        //浏览按钮的背景图片路径uploadfile.png
			        'buttonImage':'../plugins/uploadify/upload-file.png',
			        //取消上传文件的按钮图片，就是个叉叉
			        'cancel':'../plugins/uploadify/upload-cancel.png',
			        //浏览按钮的宽度
			        'width':'75',
			        //浏览按钮的高度
			        'height':'26',
			        //在浏览窗口底部的文件类型下拉菜单中显示的文本
			        'fileTypeDesc':'支持的格式',
			        //允许上传的文件后缀
			        'fileTypeExts':"*.gif;*.jpg;*.jpeg;*.png;*.doc;*.docx",
			        //上传文件的大小限制
			        'fileSizeLimit':"10MB",
			        //上传数量
			        'queueSizeLimit' : 25,
			        //开启调试
			        'debug' : false,
			        //是否自动上传
			        'auto':true,
			        //上传后是否删除
			        'removeComplete':false,
			        //清除
			        removeTimeout : 0,     
			        //超时时间
			        'successTimeout':99999,
			        //flash
			        'swf': '../plugins/uploadify/uploadify.swf',
			        //不执行默认的onSelect事件
			        'overrideEvents' : ['onDialogClose'],
			        //每次更新上载的文件的进展
			        'onUploadProgress' : function(file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal) {
			            //有时候上传进度什么想自己个性化控制，可以利用这个方法
			        },
			        //选择上传文件后调用
			        'onSelect' : function(file) {
			            //uploadBefore.text(file.name);
			            return true;
			        },
			        //返回一个错误，选择文件的时候触发
			        'onSelectError':function(file, errorCode, errorMsg){
			            switch(errorCode) {
			                case -100:
			                    alert("上传的文件数量已经超出系统限制的"+$('#uploadCompleteFile').uploadify('settings','queueSizeLimit')+"个文件！");
			                    break;
			                case -110:
			                	alert("文件 ["+file.name+"] 大小超出系统限制,最大允许2M！");
			                    break;
			                case -120:
			                    alert("文件 ["+file.name+"] 大小异常！");
			                    break;
			                case -130:
			                    alert("文件 ["+file.name+"] 类型不正确！");
			                    break;
			            }
			        },
			        //检测FLASH失败调用
			        'onFallback':function(){
			            alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
			        },
			        //上传到服务器，服务器返回相应信息到data里
			        'onUploadSuccess':function(file, data, response){
			        	//得到上传返回值
			        	var fileObj=jQuery.parseJSON(data);
			        	var $image=$("<p id='"+fileObj.fileId+"'><span>"+fileObj.fileName+"</span><span class='pl15 blue pointer'><a href='../"+fileObj.filePath+"'  target= '_blank'>下载</a></span><span class='pl15 blue pointer' fileId='"+fileObj.fileId+"'>删除</span></p>");
			        	$("#completeFileView").append($image);
			        	alert("上传成功！");
			        },
			        //上传前取消文件
			        'onCancel' : function(file) {
			        }
			    };
			};
			//上传项目图片
			$("#uploadCompleteFile").uploadify(uploadCompleteFile());
			//删除文件
			function deleteFile(fileId){
				
				var paramData={
						"dataId":fileId
				};
			    $.ajax({
			        url:"delete.jsp",
			        type:"post",
			        dataType:'text',
			        data:paramData,
			        success:function (data) {
			        	$("#"+fileId).remove();
			     	}
			    });
			};
			//删除文件
			$("#completeFileView").delegate("span","click",function(){
				var currentFileId = $(this).attr("fileId");
				deleteFile(currentFileId);
			});
			//////////////////////////////////////////////////////////////////////////////////
		});
	
	</script>
	</head>
	<body>
	    <input type="hidden" id="projectInvest" value=""/>
	    <input type="hidden" id="futureInvest" value=""/>
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
			<form name="dataForm" id="dataForm" method="post" action=<%=response.encodeURL("funds.jsp")%>>
				<input type="hidden" name="type" id="type" value="" />
				<input type="hidden" name="reportId" id="reportId" value=""/>
				<input type="hidden" name="projectId" id="projectId" value=""/>
				<div class="doArea mb5" style="text-align: center; font-weight: 600;margin-bottom: 0px;">
	            	<span class="toptip"></span>
				</div>
				<table border="0" class="inputTable">
				</table>
				<div class="doArea mb5" style="text-align: center; font-weight: 600;margin-bottom: 0px;">
	            	当月到位资金<span class="toptip"> (没有到位资金的填“0”，所填数据均取作整数)</span>
				</div>
				<table border="0" class="inputTable">
					<tr>
						<th>
							<span class="requireField">*</span>
							<label>
								自有资金：
							</label>
						</th>
						<td>
							<input type="text" class="formText projectText"
								maxlength="13" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')"  name="selfFonds" id="selfFonds"
								value="" />
						</td>
					</tr>
					<tr>
						<th>
							<span class="requireField">*</span>
							<label>
								银行贷款：
							</label>
						</th>
						<td>
							<input type="text" class="formText projectText"
								maxlength="13"  onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" name="bankLoans" id="bankLoans"
								value="" />
						</td>
					</tr>
					<tr>
						<th>
							<span class="requireField">*</span>
							<label>
								财政性资金：
							</label>
						</th>
						<td>
							<input type="text" class="formText projectText"
								maxlength="13" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')"  name="financialFunds" id="financialFunds"
								value="" />
						</td>
					</tr>
					<tr>
						<th>
							<span class="requireField">*</span>
							<label>
								其他资金：
							</label>
						</th>
						<td>
							<input type="text" class="formText projectText"
								maxlength="13" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')"  name="otherFunds" id="otherFunds"
								value="" />
						</td>
					</tr>
					<tr>
						<th>
							<span class="requireField">*</span>
							<label>
								总计：
							</label>
						</th>
						<td>
							<span id="fundsTotal"></span>
						</td>
					</tr>
					<tr style="display: none">
						<th>
							<label>
								资金到位凭证：
							</label>
						</th>
						<td>
							<div id="fundsFileView">

							</div>
							<div class="fbutton">
								<span><input id="uploadFundsFile" name="uploadFundsFile" type="file"/></span>
							</div>
							<div id="queueFundsFile"></div>
						</td>
					</tr>
				</table>
				<div class="doArea mb5" style="text-align: center; font-weight: 600;margin-bottom: 0px;">
	            	当月完成投资<span class="toptip">  (以财务实际支付为准，没有支付的不得计入)</span>
				</div>
				<table border="0" class="inputTable">
					<tr>
						<th>
							<span class="requireField">*</span>
							<label>
								当月完成投资：
							</label>
						</th>
						<td>
							<input type="text" class="formText projectText"
								maxlength="13" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" name="monthCompleteFunds" id="monthCompleteFunds"
								value="" />
						</td>
					</tr>
					<tr style="display: none">
						<th>
							<label>
								资金完成凭证：
							</label>
						</th>
						<td>
							<div id="completeFileView">

							</div>
							<div class="fbutton">
								<span><input id="uploadCompleteFile" name="uploadCompleteFile" type="file"/></span>
							</div>
							<div id="queueCompleteFile"></div>
						</td>
					</tr>
				</table>
			</form>
			<div class="buttonArea">
				<input type="button" class="formButton btn  btn-success " value="操作"  id="save" />
				&nbsp;&nbsp;
				<input type="button" class="formButton btn btn-success " value="返 回" id="back" />
			</div>
		</div>
	</body>
</html>
