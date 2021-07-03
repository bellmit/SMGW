//全局的ajax访问，处理ajax清求时异常
/*
jQuery.ajaxSetup({
   contentType : 'application/json;charset=utf-8',
   complete:function(xmlHttpRequest,textStatus){
      //通过XMLHttpRequest取得响应结果
      var res = xmlHttpRequest.responseText;
      try{
        var jsonData = JSON.parse(res);
        console.log("返回数据拦截：",jsonData);
        if(jsonData.code == '-1111'){
          //登陆页
          
          window.top.location.href=configParams.baseURL +"/login/login.html";
        }else{
          //正常情况就不统一处理了
        }
      }catch(e){
      }
    }
 });
*/
jQuery.ajaxSetup({
   contentType : 'application/json;charset=utf-8',
   complete:function(xmlHttpRequest,textStatus){
      //通过XMLHttpRequest取得响应结果
      var res = xmlHttpRequest.responseText;
      try{
        var jsonData = JSON.parse(res);
        console.log("返回数据拦截：",jsonData);
        if(jsonData.code == '-1111'){
          //登陆页
          
          window.top.location.href=configParams.baseURL +"/login/login.html";
        }else{
          //正常情况就不统一处理了
        }
      }catch(e){
      }
    }
 });