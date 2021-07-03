(function(w){
	toastr.options = {  
		"closeButton": true,
	    "debug": false,
	    "positionClass": "toast-top-right",
	    "onclick": null,
	    "showDuration": "900",
	    "hideDuration": "1000",
	    "timeOut": "8000",
	    "extendedTimeOut": "1000",
	    "showEasing": "swing",
	    "hideEasing": "linear",
	    "showMethod": "fadeIn",
	    "hideMethod": "fadeOut"
    };  
	var doc = w.document;
	var base = {
		init:function(){
			this.init_validate();//初始化验证框架
			this.init_page();//初始化分页的UI点击事件
		},
		getView:function(options){
			$.ajax({
				url:options.url,
				dataType:"text",
				type:"get",
				success:function(result){
					options.success(result);
				}
			})
		},
		getData:function(options){
			$.ajax({
				url:options.url,
				dataType:options.dataType?options.dataType:"json",
				type:"get",
				success:function(result){
					if(result.result == "fail"){
						bootbox.alert("服务异常，请稍后重试！");
					}else{
						if(options.success){
							options.success(result.data);
						}
					}
				},
				fail:function(){
					bootbox.alert("服务异常，请稍后重试！");
				}
			})
		},
		postData:function(options){
			$.ajax({
				url:options.url,
				type:"post",
				data:options.data,
				dataType:options.dataType?options.dataType:"json",
				success:function(result){
					if(result.result == "fail"){
						bootbox.alert("服务异常，请稍后重试！");
					}else{
						if(options.success){
							options.success(result.data);
						}
					}
				},
				fail:function(){
					bootbox.alert("服务异常，请稍后重试！");
				}
			})
		},
		init_page:function(){
			$("select[name=pageSize]").change(function(){
				$(".btn-success").click();
			});
			$("a[name=_pageNumber]").click(function(){
				var _pageNumber = $(this).text();
				$("input[name=pageNumber]").val(_pageNumber-1);
				$(".btn-success").parentsUntil('form').submit();
			});
			$("a[name=previous]").click(function(){
				var pageNumber = $("input[name=pageNumber]").val();
				if(pageNumber>0){
					$("input[name=pageNumber]").val(pageNumber-1);
					// $(".btn-search").click();
					$(".btn-success").parentsUntil('form').submit();
				}
			});
			$("a[name=next]").click(function(){
				var pageNumber = $("input[name=pageNumber]").val();
				var totalPages = $("#totalPages").val();
				if(pageNumber<totalPages-1){
					$("input[name=pageNumber]").val(pageNumber*1+1);
					$(".btn-success").parentsUntil('form').submit();
				}
			});
			$("#_changePageNumber").blur(function(){
				var totalPages = $("#totalPages").val();
				var _pageNumber = $(this).val();
				
				var rex = /^\d+$/g;
				if (rex.test(_pageNumber)) {
					totalPages = parseInt(totalPages, 10);
					_pageNumber = parseInt(_pageNumber, 10);
					
					if (_pageNumber > 0 && totalPages > 0) {
						if (_pageNumber > totalPages) {
							_pageNumber = totalPages;
						}
						$("input[name=pageNumber]").val(_pageNumber - 1);
						$(".btn-success").parentsUntil('form').submit();
					}
				}
			});
		},
		init_validate:function(){
			$.extend($.validator.messages, {
			    required: "该字段不能为空",
			    remote: "请修正该字段",
			    email: "请输入正确格式的电子邮件",
			    url: "请输入合法的网址",
			    date: "请输入合法的日期",
			    dateISO: "请输入合法的日期 (ISO).",
			    number: "请输入合法的数字",
			    digits: "只能输入整数",
			    creditcard: "请输入合法的信用卡号",
			    equalTo: "请再次输入相同的值",
			    accept: "请上传合法的文件",
			    maxlength: $.validator.format("请输入一个长度最多是 {0} 的字符串"),
			    minlength: $.validator.format("请输入一个长度最少是 {0} 的字符串"),
			    rangelength: $.validator.format("请输入一个长度介于 {0} 和 {1} 之间的字符串"),
			    range: $.validator.format("请输入一个介于 {0} 和 {1} 之间的值"),
			    max: $.validator.format("请输入一个最大为 {0} 的值"),
			    min: $.validator.format("请输入一个最小为 {0} 的值")
			});
			$.validator.setDefaults({
				errorClass:"alert alert-danger autowidth",
				errorElement:"div",
				highlight:false,
				submitHandler:function(form){
					$.LoadingOverlay("show");
					form.submit();
				},
				errorPlacement: function(error, element) {  
					if($(element).hasClass("select2")){
						error.insertAfter($(element).parent().children("span"));
					}else{
						error.insertAfter(element);
					}
			     }      
			})
			$("form").validate();
		},
		cookie:{
			set:function(key,value,expireDays){
				if(!expireDays){
					expireDays = 7 ;
				}
				var date = new Date();
				date.setDate(date.getDate()+expireDays);
				doc.cookie = key+"="+escape(value)+";expires="+date.toGMTString();
			},
			get:function(key){
				var ck = doc.cookie;
				if(ck.length>0){
					var sIndex = ck.indexOf(key+"=");
					if(sIndex>-1){
						sIndex = sIndex+key.length+1;
						var endIndex = ck.indexOf(";",sIndex);
						if(endIndex==-1){
							endIndex = ck.length;
						}
						return unescape(ck.substring(sIndex,endIndex))
					}
				}
			}
		},
		validAndAjaxSubmit:function($dialogObj, $listFormObj, funUnValid,successFun) {
			// console.log('validAndAjaxSubmit开始方法执行');
			if ($dialogObj.valid()) {
				var url = $dialogObj.attr("action");
				var options  = {
					url: url,//同action 
					type : 'post',
					beforeSend:function(xhr){// 请求之前
						$.LoadingOverlay("show");
						// console.log('请求之前');
					},
					success:function(data) {
						if (successFun != null && successFun != undefined) {
							try {
								successFun(data);
							} catch(e) {}
						}
						// console.log(data);
					},
					complete:function(xhr){// 请求完成
						// console.log('请求完成');
						$listFormObj.submit();
					},
					error: function(xhr,status,msg){
						// console.log("状态码"+status+"; "+msg)
						// console.log('玩命加载中..');
					}
				};
				$dialogObj.ajaxSubmit(options);
			} else {
				// console.log('验证不通过...');
				if (funUnValid != null && funUnValid != undefined) {
					try {
						funUnValid();
					} catch(e) {}
				}
			}
		},
		/* 表单验证提示 */
		showPopover : function(target, msg) {
		    target.attr("data-original-title", msg);
		    target.parent().parent('.form-group').addClass("has-error");
		    target.tooltip('show');
		},
		/* 表单提示隐藏*/
		hidPopover : function(target){
			target.parent().parent('.form-group').removeClass("has-error");
			target.parent().parent('.form-group').addClass("has-success");
			target.attr("data-original-title", "");
			target.tooltip('hide');
		}
	};
	w.base = base;
	w.base.init();
})(window);


/* -------------------------- 自定义的一些表单验证方法 ----------------------------------- */

/*
** 去了前后空格后，再验证必填
*/
$.validator.addMethod("trimRequired", function(value, element) {
	if (value == '') {
		return false;
	}
	if ($.trim(value) == '') {
		return false;
	}
	return true;
}, "该字段不能为空");

/*
** 输入内容中间不能包含空格
*/
$.validator.addMethod("unContainSpace", function(value, element) {
	if (value == '' || $.trim(value) == '') {
		return true;
	}
	if ($.trim(value).search(/\s/) != -1) {
		return false;
	}
	return true;
}, "该字段不能包含空格");


/*
** 选择文件大小的最大限制
*/
$.validator.addMethod("fileMaxSize", function(value, element, param) {
	var f = element.files;
	if (f.length == 0 ) {
		return true;
	}
	
	// 判断文件大小（字节）
	if (f[0].size > eval(param)) {
		return false;
	}
	return true;
}, "选择的文件太大");

function get_checkedAccount(){
	return parent._checkedAccount;
}

$(document).ready(function(){
	$(".btnNowUpdate").click(function(){
				$.ajax({
					type:"GET",
					url:basepath+"/version/upSql",
					dataType:"json",
					success:function(data){
						if (data.flag) {
							if (data.data.flag){
								$("#show_down_page a").attr("href",data.data.url);
	                            $("#show_down_page a")[0].click();
								toastr.info("下载程序成功，请按照升级步骤替换程序","提示信息");
							}else{
								toastr.info("已更新到最新版本，请退出后重新登录","提示信息");
							}
						}
					},
					error:function(jqXHR){
						toastr.error("更新失败","提示信息");
					}
				});
	        });
})

function setPwd(){
	var title = '修改密码';
	base.getView({
		url:basepath+"/platManager/modifyPassWordJump",
		success:function(result){
			bootbox.dialog({
				title:title,
				message:result,
				size:"large",
				buttons:{
					cancel:{
						label:"取消",
						className:"btn btn-secondary"
					},
					sure:{
						label:"确定",
						className:"btn btn-primary",
						callback:function() {
							if ($("#resetFormID").valid()) {
								// 验证通过
								var currentPassword = $(":input[name='currentPassword']").val();
								var verificationPassed = false;
								$.ajax({
									url : basepath+"/platManager/validateCurrentPassword",
									type : 'post',
									dataType : 'json',
									async : false,
									data : {"currentPassword":currentPassword},
									success : function(data) {
										//console.log(JSON.stringify(data));
										if(data.result == "success"){
											//console.log("data.result="+data.result);
											verificationPassed = true;
										} else {
											$("#renderingPrompt").val(0);
											$("#resetFormID").valid();
											$("#renderingPrompt").val(1);
										}
									}
								});
								if (verificationPassed) {
									$("#resetFormID").submit();
								}
							} else {
								console.log("验证不通过 ... ");
							}
							return false;
						}
					}
				}
			})
		}
	})
}


function toIndex(){
	window.location.href=basepath+"/";
}


function openPostWindow(url, data, name)    
{    
   var tempForm = document.createElement("form");    
   tempForm.id="tempForm1";    
   tempForm.method="post";    
   tempForm.action=url;    
   tempForm.target=name;    

   var hideInput = document.createElement("input");    
   hideInput.type="hidden";    
   hideInput.name= "content"  
   hideInput.value= JSON.stringify(data);  
   tempForm.appendChild(hideInput);     
   tempForm.addEventListener("submit",function(){ openWindow(name); });  
   document.body.appendChild(tempForm);    

   tempForm.removeEventListener("submit", null, false); 
   tempForm.submit();  
   document.body.removeChild(tempForm);  
}  
function openWindow(name)    
{    
     window.open('about:blank',name,'height=400, width=400, top=0, left=0, toolbar=yes, menubar=yes, scrollbars=yes, resizable=yes,location=yes, status=yes');     
}    


function pagerFilter(data){
	 if (typeof data.length == 'number' && typeof data.splice == 'function'){    // is array
        data = {
            total: data.length,
            rows: data
        }
    }
    var dg = $(this);
		
    var opts = dg.datagrid('options');
    var pager = dg.datagrid('getPager');
    pager.pagination({
        onSelectPage:function(pageNum, pageSize){
            opts.pageNumber = pageNum;
            opts.pageSize = pageSize;
            pager.pagination('refresh',{
                pageNumber:pageNum,
                pageSize:pageSize
            });
            dg.datagrid('loadData',data);
        }
    });
    if (!data.originalRows){
        data.originalRows = (data.rows);
    }
		if(opts.sortName){
			sortData(data.originalRows, opts.sortName, opts.sortOrder);
		}
    var start = (opts.pageNumber-1)*parseInt(opts.pageSize);
    var end = start + parseInt(opts.pageSize);
    data.rows = (data.originalRows.slice(start, end));
    return data;
}

function sortData(data, sortName, sortOrder){
	data.sort(function(a, b){
		a = a[sortName];
		b = b[sortName];
		var ret = 0;
		if(b!=undefined && a!=undefined){
			if(!isNaN(a.toString().replace(/,/g,''))&&!isNaN(b.toString().replace(/,/g,''))){//纯数字
				if(parseFloat(a.toString().replace(/,/g,'')) > parseFloat(b.toString().replace(/,/g,''))){
					ret = 1;
				}else if(parseFloat(a.toString().replace(/,/g,'')) < parseFloat(b.toString().replace(/,/g,''))){
					ret = -1;
				}
			}else{
				if(a > b){
					ret = 1;
				}else if(a < b){
					ret = -1;
				}
			}
		}
		return sortOrder == 'asc' ? ret : -ret;
	});
}

/**
 * treeGrid 工具方法
 * @type {{setPager: treeGridKit.setPager, reload: treeGridKit.reload, fitRightFrozenCol: treeGridKit.fitRightFrozenCol, handlePageParam: treeGridKit.handlePageParam}}
 */
var treeGridKit = {
    setPager: function (tg, options) {
        var p = $(tg).treegrid('getPager');
        $(p).pagination($.extend({
            beforePageText: '第',    // 页数文本框前显示的汉字
            afterPageText: '页    共 {pages} 页',
            displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
        }, options || {}));
    },
    /**
     * treegrid冻结右侧列自适应
     * @param tg
     */
    fitRightFrozenCol: function (tg) {
        $(tg).datagrid('resize', {
            width: function() {return document.body.clientWidth;}
        });
        var $datagridView1El = $('.datagrid-view1');
        var $datagridView2El = $('.datagrid-view2');
        var dgvW = $('.datagrid-view').width();
        var dgv1W = $datagridView1El.width();
        $datagridView1El.width(dgv1W);
        $datagridView2El.width(dgvW - (dgv1W));

        var $datagridView1BodyEl = $('.datagrid-view1 .datagrid-body');
        var databtableH = $datagridView1BodyEl.find('.datagrid-btable').height();
        $datagridView1BodyEl.width($datagridView1BodyEl.width() + 49);
        if (databtableH) {
            $datagridView1El.height(databtableH + 40);
        } else {
            $datagridView1El.height(40);
        }
    },
    reload: function (tg, p) {
        $(tg).treegrid('reload', p || {});
    }
    
};

/**
 * 更多查询中判断是否显示
 * aliasInfoParam：别名信息
 * aliasInfoParamDesc：格式如下{'aliasX':['A','B'...'N']} 其中aliasX为更多查询页面规定的别名，A、B、N为当前页面与后台程序交互的数据信息
 */
function  isQueryCondtion(conditionName,aliasInfoParam){
	if (conditionName in aliasInfoParam) {
	    return true;
	}else{
		return false;
	}	
};


Date.prototype.format = function(fmt) { 
    var o = { 
       "M+" : this.getMonth()+1,                 //月份 
       "d+" : this.getDate(),                    //日 
       "h+" : this.getHours(),                   //小时 
       "m+" : this.getMinutes(),                 //分 
       "s+" : this.getSeconds(),                 //秒 
       "q+" : Math.floor((this.getMonth()+3)/3), //季度 
       "S"  : this.getMilliseconds()             //毫秒 
   }; 
   if(/(y+)/.test(fmt)) {
           fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
   }
    for(var k in o) {
       if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
   return fmt;
}
//tableHeight函数，表格高度
function bootTableHeight(hval){
    //可以根据自己页面情况进行调整
	if(hval) {
		return jQuery(window).height() - hval;
	} else {
		return jQuery(window).height() - 75;
	}
}
function formatDate (date, fmt) {
    if (!date){
        return ""
    }
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    let o = {
        'M+': date.getMonth() + 1,
        'd+': date.getDate(),
        'h+': date.getHours(),
        'm+': date.getMinutes(),
        's+': date.getSeconds()
    };
    for (let k in o) {
        if (new RegExp(`(${k})`).test(fmt)) {
            let str = o[k] + '';
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? str : padLeftZero(str));
        }
    }
    return fmt;
};
function padLeftZero (str) {
    return ('00' + str).substr(str.length);
};
/**
设置表格宽度
*/
function setFixedTableBorder(table,tableBorder){
	tableBorder.width(table.width());
	tableBorder.height(0);
};