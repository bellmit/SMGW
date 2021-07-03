jQuery(document).ready(function() {
    function findYearList(){
        let now = new Date(); //当前日期
        let nowYear = now.getFullYear(); //当前年
        let projectYearNode = jQuery("#projectYear");
        projectYearNode.empty();
        for(let i=0 ; i<10 ; i++){
        	let changeYear = nowYear + i;
        	if(i==0) {
                projectYearNode.append("<option value="+ changeYear +" selected='selected' >"+ changeYear +"年</option>");
        	} else {
                projectYearNode.append("<option value="+ changeYear +" >"+ changeYear +"年</option>");
        	}
        }
    }
    findYearList();
    //得到季度
    function findQuarterList(){
        let now = new Date(); //当前日期
        let nowMonth= now.getMonth()+1;
        let nowQuarter = Math.floor( ( nowMonth % 3 == 0 ? ( nowMonth / 3 ) : ( nowMonth / 3 + 1 ) ) );
        let projectQuarterNode = jQuery("#projectQuarter");
        projectQuarterNode.empty();
        for(let i=1 ; i<=4 ; i++){
            if(nowQuarter==i) {
                projectQuarterNode.append("<option value="+ i +" selected='selected' >"+ i +"季度</option>");
            } else {
                projectQuarterNode.append("<option value="+ i +" >"+ i +"季度</option>");
            }
        }
    }
    findQuarterList();

    function findBatchList(){
        let projectBactchNode = jQuery("#batch");
        projectBactchNode.empty();
        for(let i=1 ; i<=2 ; i++){
            if(1==i) {
                projectBactchNode.append("<option value="+ i +" selected='selected' >"+ i +"</option>");
            } else {
                projectBactchNode.append("<option value="+ i +" >"+ i +"</option>");
            }
        }
    }
    findBatchList();
});