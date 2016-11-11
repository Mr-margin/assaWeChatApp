$(function () {
	var Request = new Object();
	Request = GetRequest();
	sid = Request['sid'];//帮扶人的id
	pkid = Request['pkid'];//登录人的id
	float(sid,pkid);
	savePoor();
})
//根据帮扶人列出的贫困户
function savePoor(){
	var html = '<ul class="notice-content">' ;
	$.ajax({  		       
	    url: '/assa/getSavePoorController.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {personal_id:sid},
	    success: function (data) {
	    	if(data.message == '5'){
	    		$.each(data.result,function(i,item){
		    		html += '<a href="fpdxjx.html?dz='+item.v1+item.v2+item.v3+item.v4+item.v5+'&renshu='+item.v9+'&phone='+item.v25+'&khyh='+item.v26+'&yhzh='+item.v27+'&'+
		    		'sbbz='+item.v34+'&shuxing='+item.v21+'&jls='+item.v29+'&zpyy='+item.v23+'&qtzp='+item.v33+'&rjsr='+item.v24+'&url='+item.pic_path+'&v6='+item.v6+'&poor_id='+item.pkid+'&sid='+sid+'&pkid='+pkid+'&bottom=1">'+
		    				'<li class="notice-list">'+
		    				'<table width="100%" border="0" cellspacing="0" cellpadding="0">'+
		    				'<tbody><tr><th scope="col">';
		    		if(item.v34 == '国家级贫困人口'){
		    			html += '<img src="img/country.png" style="width:60px;">'
		    		}else{
		    			html += '<img src="img/city.png" style="width:60px;">'
		    		}
		    		html += '</th> <th scope="col"><h3 class="list-tit">户主：'+item.v6+'（'+item.v9+'）</h3>'+
		    				'<p style="color:#5A5A5A; font-weight:normal; font-size:18px;">致贫原因：'+item.v23+'</p></th></tr>'+
		    				'</tbody></table></li> <span class="goin"><i class="iconfont icon-right"></i></span>';
		    		
		    	})
		    	html += '</ul>';
		    	$("#poor").html(html);
	    	}else{
	    		alert('没有帮扶的贫困户');
	    	}
	    
	    	
	    },
	    error: function (ret) { 
	    	
	    }  
	})
}