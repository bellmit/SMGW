/**
 * 封装excel下载的方法。post请求，只需要传url和对象参数即可。
 */
function downExcel(url,param,callback) {

    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.responseType = "blob";  // 返回类型blob
    // 定义请求完成的处理函数
    xhr.onload = function () {
        // 请求完成
        if (this.status === 200) {
            // $(".loadingMask").hide();
            var content = this.response;
            var aTag = document.createElement('a');
            var blob = new Blob([content]);
            var headerName = xhr.getResponseHeader("Content-disposition");
            var fileName = decodeURIComponent(headerName).substring(headerName.indexOf("filename=")+9);
            aTag.download = fileName;
            aTag.href = URL.createObjectURL(blob);
            aTag.click();
            URL.revokeObjectURL(blob);
            if (callback){
                callback()
            }
        }
    };
    // 发送ajax请求
    var formdata=new FormData();
    for(let key  in param){
        formdata.append(key, param[key]);
    }
    xhr.send(formdata);
}

/**
 * 封装前段下载，
 * @param cols 列标题
 * @param jsonData[] 需要导出的数据
 * @param filename 导出后的文件名
 */
function downCSV(cols,jsonData,filename) {

    //列标题，逗号隔开，每一个逗号就是隔开一个单元格
    let str = '';
    let arr = [];
    for (let i = 0 ; i < cols.length ; i++ ){
        str += cols[i].title+ '\t,';
        arr.push(cols[i].field);
    }
    str += '\n';
    //增加\t为了不让表格显示科学计数法或者其他格式
    for(let i = 0 ; i < jsonData.length ; i++ ){
        /*for(let item in jsonData[i]){
            str+=item.field==null?'': item.field+ '\t,';
        }*/
        for (let j =0;j<cols.length;j++){
            if (cols[j].time) {
                if (jsonData[i][cols[j].field]) {
                    let date = new Date(jsonData[i][cols[j].field]);
                    str+= formatDate(date, 'yyyy-MM-dd hh:mm:ss');
                } else {
                    str+= '-';
                }
                str+='\t,';
            }else if(cols[j].formatter){
                str+= cols[j].formatter(jsonData[i][cols[j].field]);
                str+='\t,';
            }else {
                str+=(jsonData[i][cols[j].field]==null?'': jsonData[i][cols[j].field])+ '\t,';
            }
        }
        str+='\n';
    }
    //encodeURIComponent解决中文乱码
    let uri = 'data:text/csv;charset=utf-8,\ufeff' + encodeURIComponent(str);
    //通过创建a标签实现
    let link = document.createElement("a");
    link.href = uri;
    //对下载的文件命名
    link.download = filename+'.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

function downPDF2 (select,fileName) {
    html2canvas(select, {
            dpi: 172,//导出pdf清晰度
            onrendered: function (canvas) {
                var contentWidth = canvas.width;
                var contentHeight = canvas.height;

                //一页pdf显示html页面生成的canvas高度;
                var pageHeight = contentWidth / 592.28 * 841.89;
                //未生成pdf的html页面高度
                var leftHeight = contentHeight;
                //pdf页面偏移
                var position = 0;
                //html页面生成的canvas在pdf中图片的宽高（a4纸的尺寸[595.28,841.89]）
                var imgWidth = 595.28;
                var imgHeight = 592.28 / contentWidth * contentHeight;

                var pageData = canvas.toDataURL('image/jpeg', 1.0);
                var pdf = new jsPDF('', 'pt', 'a4');

                //有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
                //当内容未超过pdf一页显示的范围，无需分页
                if (leftHeight < pageHeight) {
                    pdf.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight);
                } else {
                    while (leftHeight > 0) {
                        pdf.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
                        leftHeight -= pageHeight;
                        position -= 841.89;
                        //避免添加空白页
                        if (leftHeight > 0) {
                            pdf.addPage();
                        }
                    }
                }
                pdf.save(fileName+'.pdf');
            },
            //背景设为白色（默认为黑色）
            background: "#fff"
        })
}

//分页导出PDF文档，解决页面图片显示不全的问题
function downPDF3(select,fileName) {
		let export_content = $(select);
         let copyDom = $('<div/>');
         copyDom.addClass('super');
         copyDom.width(export_content.width() + 'px');
         copyDom.height(export_content.height() + 'px');
         $('body').append(copyDom);
         let cont = document.getElementById('export_content');
         html2canvas(export_content, {
             onrendered: function(canvas){
                 let cW = canvas.width; // 955
                     let cH = canvas.height; // 2965
                     // 一页pdf显示html页面生成的canvas高度;
                     let pageH = cW / 592.28 * 841.89; // 1357.4744208820155
					 //如果当前的pdf大于10页，那么必须要分割处理，不然canvas是没法获取到二进制字节流的
					 let pdf = new jsPDF('', 'pt', 'a4');
					 let imgW = 595.28;
					 //第一步：获取当前页面的长度具体有几个range页
					 let length = Math.ceil(cH/(pageH));

					 //第二步：循环处理每个2页块
					 for(let i=0;i<length;i++){
						 console.log("p"+i)
						 let height = pageH;
						 if(i==(length-1)){
							 height = cH-(pageH*i);
						 }
						 let ctx = canvas.getContext('2d');
						 let cutImage = ctx.getImageData(0,pageH*i,cW,height);
                     	 let pageData = createNewCanvas(cutImage,cW,height);
 						if(!pageData) console.log("error")
						// 未生成pdf的html页面高度
	                     let leftH = height; // 2965
	                     // a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
	                     let imgH = 592.28 / cW * height;
	                     // 有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
	                     if (leftH < pageH) {
			                 pdf.addImage(pageData, 'JPEG', 0, 0, imgW, imgH);
			             }else {
                             pdf.addImage(pageData, 'JPEG', 0, 0, imgW, imgH);
							 pdf.addPage();
			 			 }
					 }
					 
                     pdf.save(fileName+'.pdf');
                     
                 },
                 background: '#fff'
         });
}

/**
 * 截取canvas绘图
 * @param content
 * @param width
 * @param height
 * @returns
 */
function createNewCanvas(content,width,height){
	let nCanvas = document.createElement('canvas');
	let nCtx=nCanvas.getContext('2d');
	nCanvas.width=width;
	nCanvas.height=height;
	nCtx.putImageData(content,0,0);
	let data = nCanvas.toDataURL('image/jpeg', 1.0);
	nCtx.clearRect(0,0,width,height);  
	return data;
}
