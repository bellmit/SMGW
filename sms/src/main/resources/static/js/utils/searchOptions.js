jQuery().ready(function() {
	//得到年份
	function findYearList(){
		let now = new Date(); //当前日期
		let nowYear = now.getFullYear(); //当前年
	    let projectYearNode = jQuery("#projectYear");
	    for(let i=0 ; i<2 ; i++){
	    	let changeYear = nowYear+i;
	    	projectYearNode.append("<option value="+ changeYear +" >"+ changeYear +"</option>");
	    }
	    
	}
	findYearList();
	//得到季度
	function findQuarterList(){
		let now = new Date(); //当前日期
		let nowMonth= now.getMonth()+1;
		let nowQuarter = Math.floor( ( nowMonth % 3 == 0 ? ( nowMonth / 3 ) : ( nowMonth / 3 + 1 ) ) );  
	    let projectQuarterNode = jQuery("#projectQuarter");
	    for(let i=1 ; i<=4 ; i++){
	    	if(nowQuarter==i) {
	    		projectQuarterNode.append("<option value="+ i +" selected='selected' >"+ i +"</option>");
	    	} else {
	    		projectQuarterNode.append("<option value="+ i +" >"+ i +"</option>");
	    	}
	    }
	}
	findQuarterList();
});