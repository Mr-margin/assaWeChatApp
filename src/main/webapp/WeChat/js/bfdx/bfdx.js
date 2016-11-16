$(function () {
	var Request = new Object();
	Request = GetRequest();
	phone = Request['phone'];//帮扶人的id
	name = Request['name'];//登录人的id
	float(phone,name);
	savePoor();
})
//根据帮扶人列出的贫困户
function savePoor(){
	var html = '<ul class="notice-content">' ;
	$.ajax({  		       
	    url: '/assaWeChatApp/getSavePoorController.do',
	    type: "POST",
	    async:false,
	    dataType: 'json',
	    data: {phone:phone,name:name},
	    success: function (data) {
	    	if(data.message == '1'){
	    		$.each(data.data,function(i,item){
		    		html += '<a href="fpdxjx.html?dz='+item.v1+item.v2+item.v3+item.v4+item.v5+'&renshu='+item.v9+'&phone='+item.v25+'&khyh='+item.v26+'&yhzh='+item.v27+'&'+
		    		'sbbz='+item.v34+'&shuxing='+item.v21+'&jls='+item.v29+'&zpyy='+item.v23+'&qtzp='+item.v33+'&url='+item.pic_path+'&v6='+item.v6+'&phone='+phone+'&name='+name+'&bottom=1&pkhbh='+item.v0+'&zjhm='+item.v8+'">'+
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