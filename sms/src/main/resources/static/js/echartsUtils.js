/**
 * Created by Administrator on 2018/12/7.
 */
/**
 *
 * @author 刘中华
 * @date 2018/12/7 11:36
 * @param
 * @return
 * @Description封装echarts数据   柱状图和折线图
 */
function showEcharts(title,xAxis,echarts_data1,echarts_data2,elementId){
    // 基于准备好的dom，初始化echarts实例
    var myChart;
    if (elementId){
        myChart = echarts.init(document.getElementById(elementId));
    } else {
        myChart = echarts.init(document.getElementById('main'));
    }

    // 指定图表的配置项和数据
    var option = {
        title : {
            text: '',
        },
        tooltip : {
            trigger: 'axis'
        },
        legend: {
            data:['流入','流出']
        },
        toolbox: {
            show : true,
            x:'90%',
            feature : {
                dataView : {show: true, readOnly: true},
                magicType : {show: true, type: ['line', 'bar']},
                restore : {show: true},
                saveAsImage : {show: true},
            }
        },
        grid:{
            left:5,
            right:120,
            top:70,
            bottom:20,
            containLabel:true,
        },
        calculable : true,
        xAxis : [
            {
                type : 'category',
                axisLabel:{interval:0,rotate:-30},
                data : xAxis
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [
            {
                name:'流入',
                type:'bar',
                barMaxWidth: 40,
                data:echarts_data1,
                markPoint : {
                    data : [
                        {type : 'max', name: '最大值'},
                        {type : 'min', name: '最小值'}
                    ]
                },
                markLine : {
                    data : [
                        {type : 'average', name: '平均值'}
                    ]
                }
            },
            {
                name:'流出',
                type:'bar',
                barMaxWidth: 40,
                data:echarts_data2,
                markPoint : {
                    data : [
                        {type : 'max', name: '最大值'},
                        {type : 'min', name: '最小值'}
                    ]
                },
                markLine : {
                    data : [
                        {type : 'average', name : '平均值'}
                    ]
                }
            }
        ]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}

function showINEcharts(title,xAxis,echarts_data1,elementId){
    // 基于准备好的dom，初始化echarts实例
    var myChart;
    if (elementId){
        myChart = echarts.init(document.getElementById(elementId));
    } else {
        myChart = echarts.init(document.getElementById('main'));
    }

    // 指定图表的配置项和数据
    var option = {
        title : {
            text: '',
        },
        tooltip : {
            trigger: 'axis'
        },
        legend: {
            data:['流入']
        },
        toolbox: {
            show : true,
            x:'right',
            feature : {
                dataView : {show: true, readOnly: true},
                magicType : {show: true, type: ['line', 'bar']},
                saveAsImage : {show: true},
                restore : {show: true},
            }
        },
        grid:{
            left:5,
            right:120,
            top:70,
            bottom:20,
            containLabel:true,
        },
        calculable : true,
        xAxis : [
            {
                type : 'category',
                axisLabel:{interval:0,rotate:-30},
                data : xAxis
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [
            {
                name:'流入',
                type:'bar',
                barMaxWidth: 40,
                data:echarts_data1,
                markPoint : {
                    data : [
                        {type : 'max', name: '最大值'},
                        {type : 'min', name: '最小值'}
                    ]
                },
                markLine : {
                    data : [
                        {type : 'average', name: '平均值'}
                    ]
                }
            }
        ]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}

function showOUTEcharts(title,xAxis,echarts_data2,elementId){
    // 基于准备好的dom，初始化echarts实例
    var myChart;
    if (elementId){
        myChart = echarts.init(document.getElementById(elementId));
    } else {
        myChart = echarts.init(document.getElementById('main'));
    }

    // 指定图表的配置项和数据
    var option = {
        title : {
            text: '',
        },
        tooltip : {
            trigger: 'axis'
        },
        legend: {
            data:['流出']
        },
        toolbox: {
            show : true,
            x:'90%',
            feature : {
                dataView : {show: true, readOnly: true},
                magicType : {show: true, type: ['line', 'bar']},
                restore : {show: true},
                saveAsImage : {show: true},
            }
        },
        grid:{
            left:5,
            right:120,
            top:70,
            bottom:20,
            containLabel:true,
        },
        calculable : true,
        xAxis : [
            {
                type : 'category',
                axisLabel:{interval:0,rotate:-30},
                data : xAxis
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [
            {
                name:'流出',
                type:'bar',
                barMaxWidth: 40,
                data:echarts_data2,
                markPoint : {
                    data : [
                        {type : 'max', name: '最大值'},
                        {type : 'min', name: '最小值'}
                    ]
                },
                markLine : {
                    data : [
                        {type : 'average', name : '平均值'}
                    ]
                }
            }
        ]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}

/**
 *
 * @author 刘中华
 * @date 2018/12/7 11:36
 * @param
 * @return
 * @Description封装echarts数据   饼状图
 */
function showEcharts_cake(id,title,xAxis,echarts_data){

    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById(id));
    // 指定图表的配置项和数据
    option = {
        title : {
            text: title,
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: xAxis
        },
        toolbox: {
            show : true,
            x:'90%',
            feature : {
                saveAsImage : {show: true}
            }
        },
        series : [
            {
                name: title,
                type: 'pie',
                minAngle: 5,
                avoidLabelOverlap: true,
                radius : '55%',
                center: ['50%', '60%'],
                data:echarts_data,
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                label:{
                    align: 'left',
                    normal:{
                        formatter(v) {
                            let text = Math.round(v.percent)+'%' + '' + v.name
                            if(text.length <= 8)
                            {
                                return text;
                            }else if(text.length > 8 && text.length <= 16){
                                return text = `${text.slice(0,8)}\n${text.slice(8)}`
                            }else if(text.length > 16 && text.length <= 24){
                                return text = `${text.slice(0,8)}\n${text.slice(8,16)}\n${text.slice(16)}`
                            }else if(text.length > 24 && text.length <= 30){
                                return text = `${text.slice(0,8)}\n${text.slice(8,16)}\n${text.slice(16,24)}\n${text.slice(24)}`
                            }else if(text.length > 30){
                                return text = `${text.slice(0,8)}\n${text.slice(8,16)}\n${text.slice(16,24)}\n${text.slice(24,30)}\n${text.slice(30)}`
                            }
                        },
                        textStyle : {
                            fontSize : 8
                        }
                    }
                }
            }
        ]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}
/**
*
* @author 
* @date 2018/12/7 11:36
* @param
* @return
* @Description封装echarts数据   柱状图和折线图
*/
function showEcharts4z(legend,xAxis,echarts_data0,echarts_data_txt,echarts_data1,echarts_data2_txt,echarts_data2){

   // 基于准备好的dom，初始化echarts实例
   var myChart = echarts.init(document.getElementById('main'));
   // 指定图表的配置项和数据
   var option = {
       title : {
           text: ''
       },
       tooltip : {
           trigger: 'axis'
       },
       legend: {
           data:[legend],
           type:'scroll',
       },
       toolbox: {
           show : true,
           x:'90%',
           feature : {
               dataView : {show: true, readOnly: true},
               magicType : {show: true, type: ['line', 'bar']},
               restore : {show: true},
               saveAsImage : {show: true}
           }
       },
       grid:{
           left:5,
           right:120,
           top:70,
           bottom:20,
           containLabel:true,
       },
       calculable : true,
       xAxis : [
           {
               type : 'category',
               data : xAxis,
               axisLabel:{interval:0,rotate:-30},
           },

       ],
       yAxis : [
           {
               type : 'value',
           }
       ],
       series : [
           {
               name:legend,
               type:'bar',
               barMaxWidth: 40,
               data:echarts_data0,
               markPoint : {
                   data : [
                       {type : 'max', name: '最大值'},
                       {type : 'min', name: '最小值'}
                   ]
               },
               markLine : {
                   data : [
                       {type : 'average', name: '平均值'}
                   ]
               }
           }
       ],

   };

	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
	myChart.on('click', function (params) {
		console.log(params);
	});
}

/**
 * 首页点击后展示详情
 * @param legend
 * @param xAxis
 * @param echarts_data0
 * @returns
 */
function showEcharts4Home(legend,xAxis,echarts_data0){

   // 基于准备好的dom，初始化echarts实例
   var myChart = echarts.init(document.getElementById('main'));
   // 指定图表的配置项和数据
   var option = {
       title : {
           text: ''
       },
       tooltip : {
           trigger: 'axis'
       },
       legend: {
           data:[legend],
           type:'scroll',
       },
       toolbox: {
           show : true,
           feature : {
               dataView : {show: true, readOnly: true},
               magicType : {show: true, type: ['line', 'bar']},
               restore : {show: true},
               saveAsImage : {show: true}
           }
       },
       grid:{
           left:5,
           right:120,
           top:70,
           bottom:20,
           containLabel:true,
       },
       calculable : true,
       xAxis : [
           {
               type : 'category',
               data : xAxis,
               axisLabel:{interval:0,rotate:-30},
           },

       ],
       yAxis : [
           {
               type : 'value',
           }
       ],
       series : [
           {
               name:legend,
               type:'bar',
               barMaxWidth: 40,
               data:echarts_data0,
               markPoint : {
                   data : [
                       {type : 'max', name: '最大值'},
                       {type : 'min', name: '最小值'}
                   ]
               },
               markLine : {
                   data : [
                       {type : 'average', name: '平均值'}
                   ]
               }
           }
       ],

   };

	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
	var is_submit=false;
	myChart.on('click', function (params) {
		if(is_submit){
			return;
		}
		is_submit=true;
		var title = "共同账户交易详情";
		var j = 0;
		base.getView({
			url:"getECDetails4AccountBodyInit?accountBody="+params.name,
			success:function(result){
				bootbox.dialog({
					title:title,
					message:result,
					boxCss:{"width":"1300px"},
					closeButton : true
				})
				is_submit=false;
			}
		});
	});
}

/**
 *
 * @author
 * @date 2018/12/7 11:36
 * @param 刘中华
 * @return
 * @Description封装echarts数据   折线图
 */
function showEcharts_BrokenLine(id,title,xAxis,echarts_data){
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById(id));
    // 指定图表的配置项和数据
    option = {
        title : {
            text: title,
            x:'center'
        },
        tooltip: {
            trigger: 'axis'
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        grid:{
            left:5,
            right:120,
            top:70,
            bottom:20,
            containLabel:true,
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: xAxis,
            axisLabel:{interval:0,rotate:-30},
        },
        yAxis: {
            type: 'value'
        },
        series: [{
            data: echarts_data,
            type: 'line'
        }]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}


